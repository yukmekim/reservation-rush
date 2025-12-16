package dev.reservation.reservation_rush.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.reservation.reservation_rush.common.response.Response;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<Void>> handleBusinessException(BusinessException e) {
        int status = e.getErrorCode().getStatus().value();
        
        return ResponseEntity.status(status)
                .body(Response.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
    }
}
