package dev.reservation.rush.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.reservation.rush.common.response.Response;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비지니스 로직의 공통 예외 처리를 합니다.
     * 
     * @param e 비즈니스 예외
     * @return Response.error()
     * @see Response
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<Void>> handleBusinessException(BusinessException e) {
        int status = e.getErrorCode().getStatus().value();

        return ResponseEntity.status(status)
                .body(Response.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
    }

    /**
     * Validation 예외 처리 (400 Bad Request)
     * 
     * @param e 비즈니스 예외
     * @return Response.error()
     * @see Response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleValidationException(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        
        // DTO에 적은 message 중 첫 번째 것을 가져옴
        String detailMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        
        // 없을 경우 기본 메시지 사용
        if (detailMessage == null) {
            detailMessage = errorCode.getMessage();
        }

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(Response.error(errorCode.getCode(), detailMessage));
    }

    /**
     * HTTP 메서드 미지원 예외 처리 (405 Method Not Allowed)
     * 
     * 
     * @param e 비즈니스 예외
     * @return Response.error()
     * @see Response
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(Response.error(errorCode.getCode(), errorCode.getMessage()));
    }

    /**
     * 정의되지 않은 서버 내부 오류를 처리합니다.
     * 모든 예외를 catch하여 서버 오류로 응답합니다.
     * 
     * @param e 모든 예외
     * @return Response.error()
     * @see Response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleException(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(errorCode.getStatus().value())
                .body(Response.error(errorCode.getCode(), errorCode.getMessage()));
    }
}
