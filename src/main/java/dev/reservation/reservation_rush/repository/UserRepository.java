package dev.reservation.reservation_rush.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.reservation.reservation_rush.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
