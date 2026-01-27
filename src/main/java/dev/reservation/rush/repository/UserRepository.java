package dev.reservation.rush.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.reservation.rush.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
