package dev.reservation.reservation_rush.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Response<T> {
    private boolean success;
    private String message;
    private String code;
    private T data;

    public static <T> Response<T> success(String code, String message, T data) {
        return Response.<T>builder()
                .success(true)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> Response<T> success(T data, String message) {
        return Response.success("200", message, data);
    }

    public static <T> Response<T> success(String message) {
        return Response.success("200", message, null);
    }

    public static <T> Response<T> error(String code, String message) {
        return Response.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }
}
