package dev.reservation.rush.common.response;

public record Response<T>(
    boolean success,
    T data,
    String code,
    String message
) {
    public static <T> Response<T> success(String code, String message, T data) {
        return new Response<>(true, data, code, message);
    }

    public static <T> Response<T> success(T data, String message) {
        return success("200", message, data);
    }

    public static <T> Response<T> success(String message) {
        return success("200", message, null);
    }

    // 에러 응답
    public static <T> Response<T> error(String code, String message) {
        return new Response<>(false, null, code, message);
    }
}
