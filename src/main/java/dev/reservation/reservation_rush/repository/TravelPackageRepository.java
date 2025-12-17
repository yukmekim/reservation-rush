package dev.reservation.reservation_rush.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.reservation.reservation_rush.entity.TravelPackage;
import jakarta.persistence.LockModeType;

@Repository
public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TravelPackage t WHERE t.id = :id")
    Optional<TravelPackage> findByIdWithLock(@Param("id") Long id);
}
