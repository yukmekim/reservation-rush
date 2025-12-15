package dev.reservation.reservation_rush;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ReservationRushApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationRushApplication.class, args);
	}

}
