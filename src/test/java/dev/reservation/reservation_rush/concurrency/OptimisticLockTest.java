package dev.reservation.reservation_rush.concurrency;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.assertj.core.api.Assertions.assertThat;

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
import dev.reservation.rush.dto.response.TravelPackageResponse;
import dev.reservation.rush.entity.User;
import dev.reservation.rush.repository.BookingRepository;
import dev.reservation.rush.repository.TravelPackageRepository;
import dev.reservation.rush.repository.UserRepository;
import dev.reservation.rush.service.BookingService;
import dev.reservation.rush.service.TravelPackageService;

@SpringBootTest
@DisplayName("Optimistic Lock 동시성 제어")
public class OptimisticLockTest {
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
        bookingRepository.deleteAll();
        travelPackageRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .name("테스트 사용자")
                .build();
        userId = userRepository.save(user).getId();

        TravelPackageCreateRequest packageRequest = new TravelPackageCreateRequest(
                "제주도 3박4일",
                "Optimistic Lock 테스트용 패키지",
                "제주",
                BigDecimal.valueOf(500000),
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(13),
                10);
        TravelPackageResponse packageResponse = travelPackageService.createPackage(packageRequest);
        packageId = packageResponse.id();

        System.out.println("=== 테스트 초기 상태 ===");
        System.out.println("패키지 ID: " + packageId);
        System.out.println("총 좌석: 10");
        System.out.println("가용 좌석: 10");
        System.out.println();
    }

    @Test
    @DisplayName("Optimistic Lock으로 32개 동시 요청 → 재시도 후 정확히 10개만 성공")
    void shouldPreventOverbookingWithOptimisticLock() throws InterruptedException {
        int threadCount = 32;
        ExecutorService executorService = newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        System.out.println("=== Optimistic Lock 테스트 시작 ===");
        System.out.println("동시 요청 수: " + threadCount);
        System.out.println();

        CountDownLatch startLatch = new CountDownLatch(1);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    BookingCreateRequest request = new BookingCreateRequest(userId, packageId);
                    bookingService.createBookingWithOptimisticLock(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        startLatch.countDown();
        latch.await();
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        TravelPackageResponse finalPackage = travelPackageService.getPackage(packageId);
        long actualBookingCount = bookingRepository.count();

        System.out.println("=== 테스트 결과 ===");
        System.out.println("예약 성공: " + successCount.get() + "건");
        System.out.println("예약 실패: " + failCount.get() + "건");
        System.out.println("DB 예약 건수: " + actualBookingCount + "건");
        System.out.println("최종 가용 좌석: " + finalPackage.availableSeats());
        System.out.println();
        
        System.out.println("=== 성능 측정 ===");
        System.out.println("총 실행 시간: " + totalTime + "ms");
        System.out.println("처리량: " + String.format("%.2f", (double) threadCount / (totalTime / 1000.0)) + " req/sec");
        System.out.println();

        assertThat(actualBookingCount).isEqualTo(10);
        assertThat(successCount.get()).isEqualTo(10);
        assertThat(failCount.get()).isEqualTo(22);
        assertThat(finalPackage.availableSeats()).isEqualTo(0);

        System.out.println("✅ Optimistic Lock 동시성 제어 성공!");
        System.out.println("   - 오버부킹 방지: 10/10");
        System.out.println("   - Lost Update 방지: 좌석 정확히 0");
    }
}
