package dev.reservation.rush.service.facade;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import dev.reservation.rush.common.exception.BusinessException;
import dev.reservation.rush.common.exception.ErrorCode;
import dev.reservation.rush.dto.request.BookingCreateRequest;
import dev.reservation.rush.dto.response.BookingResponse;
import dev.reservation.rush.service.BookingService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingFacade {
    private final RedissonClient redissonClient;

    private final BookingService bookingService;

    public BookingResponse createBooking(BookingCreateRequest request) {
        String lockKey = "package:lock:" + request.packageId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // Lock 획득 시도: 최대 5초 대기, 10초 후 자동 해제
            boolean acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);

            if (!acquired) {
                throw new BusinessException(ErrorCode.LOCK_ACQUISITION_FAILED);
            }

            return bookingService.createBookingWithRedisLock(request);
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
