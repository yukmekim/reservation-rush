package dev.reservation.rush.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import dev.reservation.rush.domain.TravelPackage;
import dev.reservation.rush.domain.User;
import dev.reservation.rush.repository.TravelPackageRepository;
import dev.reservation.rush.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeedDataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final TravelPackageRepository travelPackageRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("🌱 Starting seed data initialization...");

        if (userRepository.count() > 0) {
            log.info("✅ Seed data already exists. Skipping initialization");
            return;
        }
        initUsers();
        initTravelPackages();

        log.info("✅ Seed data initialization completed!");
    }

    private void initUsers() {
        log.info("👤 Initializing users...");

        User user = User.builder()
                .name("육장훈")
                .build();

        userRepository.save(user);

        log.info("✅ Created {} users", 1);
    }

    private void initTravelPackages() {
        log.info("✈️ Initializing travel packages...");

        TravelPackage jeju = TravelPackage.builder()
                .name("제주도 3박4일 힐링 투어")
                .description("제주의 아름다운 자연을 만끽하는 여행")
                .location("제주도")
                .startDate(LocalDateTime.of(2025, 3, 15, 9, 0))
                .endDate(LocalDateTime.of(2025, 3, 18, 18, 0))
                .price(new BigDecimal("500000"))
                .totalSeats(30)
                .availableSeats(30)
                .build();

        TravelPackage busan = TravelPackage.builder()
                .name("일본 다카마쓰 3박4일 우동 투어")
                .description("하루 20끼 가능한 20끼 형들에게 추천, 우동 부수러 갈 사람")
                .location("다카마쓰")
                .startDate(LocalDateTime.of(2025, 3, 20, 10, 0))
                .endDate(LocalDateTime.of(2025, 3, 22, 17, 0))
                .price(new BigDecimal("800000"))
                .totalSeats(20)
                .availableSeats(20)
                .build();

        TravelPackage gangneung = TravelPackage.builder()
                .name("강릉 1박2일 힐링 여행")
                .description("강릉의 카페와 해변을 둘러보는 여행")
                .location("강릉")
                .startDate(LocalDateTime.of(2025, 4, 1, 8, 0))
                .endDate(LocalDateTime.of(2025, 4, 2, 19, 0))
                .price(new BigDecimal("200000"))
                .totalSeats(15)
                .availableSeats(15)
                .build();

        travelPackageRepository.save(jeju);
        travelPackageRepository.save(busan);
        travelPackageRepository.save(gangneung);

        log.info("✅ Created {} travel packages", 3);
    }
}
