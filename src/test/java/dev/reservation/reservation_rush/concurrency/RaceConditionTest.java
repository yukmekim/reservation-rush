package dev.reservation.reservation_rush.concurrency;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.reservation.rush.dto.request.BookingCreateRequest;
import dev.reservation.rush.dto.request.TravelPackageCreateRequest;
import dev.reservation.rush.dto.response.BookingResponse;
import dev.reservation.rush.dto.response.TravelPackageResponse;
import dev.reservation.rush.entity.User;
import dev.reservation.rush.repository.BookingRepository;
import dev.reservation.rush.repository.TravelPackageRepository;
import dev.reservation.rush.repository.UserRepository;
import dev.reservation.rush.service.BookingService;
import dev.reservation.rush.service.TravelPackageService;

/**
 * Phase 1: Race Condition 재현 테스트
 *
 * 목적: 동시성 제어 없이 오버부킹 문제 발생을 증명
 * 기대: 10개 좌석에 100개 동시 요청 시 오버부킹 발생
 */
@SpringBootTest
@DisplayName("Phase 1: Race Condition 재현")
class RaceConditionTest {

    @Autowired
    private TravelPackageService travelPackageService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TravelPackageRepository travelPackageRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    private Long packageId;
    private Long userId;

    @BeforeEach
    void setUp() {
        // 데이터 초기화
        bookingRepository.deleteAll();
        travelPackageRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트 사용자 생성
        User user = User.builder()
                .name("테스트 사용자")
                .build();
        userId = userRepository.save(user).getId();

        // 테스트 패키지 생성 (좌석 10개)
        TravelPackageCreateRequest packageRequest = new TravelPackageCreateRequest(
                "제주도 3박4일",
                "동시성 테스트용 패키지",
                "제주",
                BigDecimal.valueOf(500000),
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(13),
                10 // 총 좌석 10개
        );
        TravelPackageResponse packageResponse = travelPackageService.createPackage(packageRequest);
        packageId = packageResponse.id();

        System.out.println("=== 테스트 초기 상태 ===");
        System.out.println("패키지 ID: " + packageId);
        System.out.println("총 좌석: 10");
        System.out.println("가용 좌석: 10");
        System.out.println();
    }

    @Test
    @DisplayName("동시성 제어 없이 100개 동시 요청 → 오버부킹 발생")
    void shouldCauseOverbookingWithoutConcurrencyControl() throws InterruptedException {
        // Given
        int threadCount = 32;
        ExecutorService executorService = newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When: 100개의 스레드가 동시에 예약 요청
        System.out.println("=== 동시성 테스트 시작 ===");
        System.out.println("동시 요청 수: " + threadCount);
        System.out.println();

        CountDownLatch startLatch = new CountDownLatch(1); // 동시 출발 신호용

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await(); // 출발 신호 대기
                    BookingCreateRequest request = new BookingCreateRequest(userId, packageId);
                    BookingResponse response = bookingService.createBooking(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        startLatch.countDown(); // 카운트다운: 모든 스레드 동시 시작!

        latch.await(); // 모든 스레드 완료 대기
        executorService.shutdown();

        // Then: 결과 검증
        TravelPackageResponse finalPackage = travelPackageService.getPackage(packageId);
        long actualBookingCount = bookingRepository.count();

        System.out.println("=== 테스트 결과 ===");
        System.out.println("예약 성공: " + successCount.get() + "건");
        System.out.println("예약 실패: " + failCount.get() + "건");
        System.out.println("DB 예약 건수: " + actualBookingCount + "건");
        System.out.println("최종 가용 좌석: " + finalPackage.availableSeats());
        System.out.println();

        // 검증: Race Condition 발생 확인
        assertThat(actualBookingCount).isGreaterThan(10);
        System.out.println("오버부킹 발생 확인: " + (actualBookingCount - 10) + "건");

        // 검증: Lost Update 발생 확인
        int expectedSeatsIfAllUpdated = 10 - (int) actualBookingCount;
        assertThat(finalPackage.availableSeats()).isGreaterThan(expectedSeatsIfAllUpdated);
        System.out.println("Lost Update 발생: 예상 " + expectedSeatsIfAllUpdated + "석, 실제 " + finalPackage.availableSeats() + "석");
    }

    @Test
    @DisplayName("순차 처리 시 정상 동작 확인 (10개 예약 후 11번째 실패)")
    void shouldWorkCorrectlyWithSequentialRequests() {
        // Given: 좌석 10개인 패키지

        // When: 10개 순차 예약
        for (int i = 0; i < 10; i++) {
            BookingCreateRequest request = new BookingCreateRequest(userId, packageId);
            bookingService.createBooking(request);
        }

        // Then: 10개 예약 성공
        TravelPackageResponse packageAfter10 = travelPackageService.getPackage(packageId);
        assertThat(packageAfter10.availableSeats()).isEqualTo(0);
        assertThat(bookingRepository.count()).isEqualTo(10);

        // When: 11번째 예약 시도
        BookingCreateRequest request11 = new BookingCreateRequest(userId, packageId);

        // Then: 예약 실패 (좌석 부족)
        assertThatThrownBy(() -> bookingService.createBooking(request11))
                .hasMessageContaining("예약 가능한 좌석이 없습니다");

        System.out.println("순차 예약: 정상 동작 확인");
        System.out.println("   - 10개 예약 성공, 11번째 실패");
    }
}
