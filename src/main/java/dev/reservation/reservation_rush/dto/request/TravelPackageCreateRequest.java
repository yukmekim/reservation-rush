package dev.reservation.reservation_rush.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import dev.reservation.reservation_rush.entity.TravelPackage;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TravelPackageCreateRequest(
    @NotBlank(message = "패키지 이름은 필수입니다.") 
    String name,

    @Size(max = 500, message = "설명은 최대 500자까지 입력 가능합니다.")
    String description,

    @NotBlank(message = "장소는 필수입니다.")
    String location,

    @NotNull(message = "가격은 필수입니다.")
    @Positive(message = "가격은 0보다 커야 합니다.")
    BigDecimal price,

    @NotNull(message = "시작일은 필수입니다.")
    @Future(message = "시작일은 현재일 이후여야 합니다.")
    LocalDateTime startDate,

    @NotNull(message = "종료일은 필수입니다.")
    @Future(message = "종료일은 시작일 이후여야 합니다.")
    LocalDateTime endDate,

    @NotNull(message = "총 좌석 수는 필수입니다.")
    @Positive(message = "총 좌석 수는 0보다 커야 합니다.")
    Integer totalSeats
) {
    public TravelPackage toEntity() {
        return TravelPackage.builder()
                .name(name)
                .description(description)
                .location(location)
                .price(price)
                .startDate(startDate)
                .endDate(endDate)
                .totalSeats(totalSeats)
                .availableSeats(totalSeats)
                .build();
    }
}
