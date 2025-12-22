package dev.reservation.rush.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // ========== Common Errors (C) ==========
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "지원하지 않는 HTTP 메서드입니다."),

    // ========== Redis Errors (R) ==========
    LOCK_ACQUISITION_FAILED(HttpStatus.REQUEST_TIMEOUT, "R001", "락 획득에 실패했습니다"),
    LOCK_INTERRUPTED(HttpStatus.INTERNAL_SERVER_ERROR, "R002", "락 획득 중 인터럽트가 발생했습니다"),

    // ========== Server Errors (S) ==========
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 오류가 발생했습니다."),

    // ========== Package Errors (P) ==========
    PACKAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "해당 패키지를 찾을 수 없습니다."),
    PACKAGE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "P002", "이미 존재하는 패키지입니다."),
    PACKAGE_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "P003", "사용할 수 없는 패키지입니다."),
    SOLD_OUT_PACKAGE(HttpStatus.CONFLICT, "P004", "예약 가능한 좌석이 없습니다."),
    SEAT_OVERFLOW(HttpStatus.INTERNAL_SERVER_ERROR, "P005", "예약 가능 좌석 수가 최대 좌석 수를 초과할 수 없습니다."),

    // ========== Booking Errors (B) ==========
    ALREADY_CANCELLED_BOOKING(HttpStatus.BAD_REQUEST, "B001", "이미 취소된 예약입니다."),
    CANNOT_CONFIRM_CANCELLED_BOOKING(HttpStatus.BAD_REQUEST, "B002", "이미 취소된 예약은 확정할 수 없습니다."),
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND, "B003", "예약을 찾을 수 없습니다."),
    BOOKING_FAILED_AFTER_RETRIES(HttpStatus.INTERNAL_SERVER_ERROR, "B004", "예약 재시도 횟수를 초과했습니다."),
    BOOKING_INTERRUPTED(HttpStatus.INTERNAL_SERVER_ERROR, "B005", "예약이 중단되었습니다."),

    // ========== User Errors (U) ==========
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
