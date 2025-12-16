package dev.reservation.reservation_rush.dto.request;

import jakarta.validation.constraints.NotNull;

public record BookingCreateRequest(
    @NotNull(message = "사용자 ID는 필수입니다.")
    Long userId,

    @NotNull(message = "패키지 ID는 필수입니다.")
    Long packageId
) {
}
