package dev.reservation.reservation_rush.service;

import java.util.List;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    /**
     * [version.1] 
     * Service Layer 내부에서 하나의 메서드에서 락 관리와 트랜잭션을 동시에 처리
     * TODO 트랜잭션 완료전에 락이 풀리는 문제 확인 및 수정 필요
     * [version.2]
     * Facade Layer에서 redis 락 관리
     * Service Layer에서 트랜잭션 관리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BookingResponse createBookingWithRedisLock(BookingCreateRequest request) {
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
    }
}
