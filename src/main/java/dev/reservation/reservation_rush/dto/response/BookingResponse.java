package dev.reservation.reservation_rush.dto.response;

import java.time.LocalDateTime;

import dev.reservation.reservation_rush.entity.Booking;
import dev.reservation.reservation_rush.enums.BookingStatus;

public record BookingResponse(
    Long id,
    Long userId,
    String userName,
    Long packageId,
    String packageName,
    BookingStatus status,
    LocalDateTime createdAt
) {  
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
            booking.getId(),
            booking.getUser().getId(),
            booking.getUser().getName(),
            booking.getTravelPackage().getId(),
            booking.getTravelPackage().getName(),
            booking.getStatus(),
            booking.getCreatedAt()
        );
    }
}
