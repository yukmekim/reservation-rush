package dev.reservation.reservation_rush.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.reservation.reservation_rush.entity.TravelPackage;

@Repository
public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long> {
}
