package dev.reservation.reservation_rush.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    PACKAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "Package not found"),
    PACKAGE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "R002", "Package already exists"),
    PACKAGE_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "R003", "Package not available"),
    SOLD_OUT_PACKAGE(HttpStatus.CONFLICT, "P001", "예약 가능한 좌석이 없습니다."),
    SEAT_OVERFLOW(HttpStatus.INTERNAL_SERVER_ERROR, "P002", "예약 가능 좌석 수가 최대 좌석 수를 초과할 수 없습니다."),
    
    ALREADY_CANCELLED_BOOKING(HttpStatus.BAD_REQUEST, "B001", "이미 취소된 예약입니다."),
    CANNOT_CONFIRM_CANCELLED_BOOKING(HttpStatus.BAD_REQUEST, "B002", "이미 취소된 예약은 확정할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
