package dev.reservation.reservation_rush.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.reservation.reservation_rush.common.exception.BusinessException;
import dev.reservation.reservation_rush.common.exception.ErrorCode;
import dev.reservation.reservation_rush.dto.request.BookingCreateRequest;
import dev.reservation.reservation_rush.dto.response.BookingResponse;
import dev.reservation.reservation_rush.entity.Booking;
import dev.reservation.reservation_rush.entity.TravelPackage;
import dev.reservation.reservation_rush.entity.User;
import dev.reservation.reservation_rush.enums.BookingStatus;
import dev.reservation.reservation_rush.repository.BookingRepository;
import dev.reservation.reservation_rush.repository.TravelPackageRepository;
import dev.reservation.reservation_rush.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {
    private final UserRepository userRepository;
    private final TravelPackageRepository travelPackageRepository;
    private final BookingRepository bookingRepository;

    private final RedissonClient redissonClient;

    // 예약 생성 - 동시성 이슈
    @Transactional
    public BookingResponse createBooking(BookingCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        TravelPackage travelPackage = travelPackageRepository.findById(request.packageId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PACKAGE_NOT_FOUND));

        travelPackage.decreaseSeats();

        Booking booking = Booking.builder()
                .user(user)
                .travelPackage(travelPackage)
                .build();

        return BookingResponse.from(bookingRepository.save(booking));
    }

    // 예약 생성 - Pessimistic Lock
    @Transactional
    public BookingResponse createBookingWithPessimisticLock(BookingCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        TravelPackage travelPackage = travelPackageRepository.findByIdWithLock(request.packageId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PACKAGE_NOT_FOUND));

        travelPackage.decreaseSeats();

        Booking booking = Booking.builder()
                .user(user)
                .travelPackage(travelPackage)
                .build();

        return BookingResponse.from(bookingRepository.save(booking));
    }

    // 예약 생성 - Optimistic Lock
    @Transactional
    @Retryable(
        retryFor = {
                ObjectOptimisticLockingFailureException.class,
                OptimisticLockException.class
        },
        maxAttempts = 100,
        backoff = @Backoff(delay = 50, maxDelay = 100, random = true)
    )
    public BookingResponse createBookingWithOptimisticLock(BookingCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        TravelPackage travelPackage = travelPackageRepository.findById(request.packageId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PACKAGE_NOT_FOUND));

        travelPackage.decreaseSeats();

        Booking booking = Booking.builder()
                .user(user)
                .travelPackage(travelPackage)
                .status(BookingStatus.PENDING)
                .build();
        return BookingResponse.from(bookingRepository.save(booking));
    }

    public List<BookingResponse> getBookings(Long packageId) {
        return bookingRepository.findAllByTravelPackageId(packageId).stream()
                .map(BookingResponse::from)
                .toList();
    }


    // TODO - 트랜젝션 완료전에 락이 풀리는 문제 확인 수정 필요
    @Transactional
    public BookingResponse createBookingWithRedisLock(BookingCreateRequest request) {
        String lockKey = "package:lock:" + request.packageId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
        // Lock 획득 시도: 최대 5초 대기, 10초 후 자동 해제
            boolean acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);

            if (!acquired) {
                throw new BusinessException(ErrorCode.LOCK_ACQUISITION_FAILED);      
            }

            // Lock 획득 성공 → 예약 처리
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            TravelPackage travelPackage = travelPackageRepository
                    .findById(request.packageId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PACKAGE_NOT_FOUND));

            travelPackage.decreaseSeats();

            Booking booking = Booking.builder()
                    .user(user)
                    .travelPackage(travelPackage)
                    .status(BookingStatus.PENDING)
                    .build();

            return BookingResponse.from(bookingRepository.save(booking));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.LOCK_INTERRUPTED);
        } finally {
            // Lock 해제 (현재 스레드가 Lock을 소유한 경우에만)
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
