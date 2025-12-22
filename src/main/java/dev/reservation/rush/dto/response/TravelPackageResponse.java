package dev.reservation.rush.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import dev.reservation.rush.entity.TravelPackage;

public record TravelPackageResponse(
    Long id,
    String name,
    String description,
    String location,
    BigDecimal price,
    LocalDateTime startDate,
    LocalDateTime endDate,
    int totalSeats,
    int availableSeats
) {
    public static TravelPackageResponse from(TravelPackage travelPackage) {
        return new TravelPackageResponse(
            travelPackage.getId(),
            travelPackage.getName(),
            travelPackage.getDescription(),
            travelPackage.getLocation(),
            travelPackage.getPrice(),
            travelPackage.getStartDate(),
            travelPackage.getEndDate(),
            travelPackage.getTotalSeats(),
            travelPackage.getAvailableSeats()
        );
    }
}
