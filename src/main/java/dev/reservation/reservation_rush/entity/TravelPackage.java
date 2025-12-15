package dev.reservation.reservation_rush.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "travel_package")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TravelPackage extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @Comment("여행 패키지 이름")
    private String name;

    @Column(name = "description")
    @Comment("여행 패키지 설명")
    private String description;

    @Column(name = "location", nullable = false)
    @Comment("여행지")
    private String location;

    @Column(name = "start_date", nullable = false)
    @Comment("여행 시작일")
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    @Comment("여행 종료일")
    private LocalDateTime endDate;

    @Column(name = "price", nullable = false)
    @Comment("가격")
    private BigDecimal price;

    @Column(name = "available_seats", nullable = false)
    @Comment("예약 가능 좌석 수")
    private int availableSeats;

    @Column(name = "total_seats", nullable = false)
    @Comment("총 좌석 수")
    private int totalSeats;

    public boolean isAvailable() {
        return availableSeats > 0;
    }

    public void decreaseSeats() {
        validateAvailableSeats();
        this.availableSeats--;
    }

    public void increaseSeats() {
        if (this.availableSeats >= this.totalSeats) {
            throw new IllegalArgumentException("예약 가능 좌석 수가 최대 좌석 수를 초과할 수 없습니다.");
        }
        this.availableSeats++;
    }

    private void validateAvailableSeats() {
        if (this.availableSeats <= 0) {
            throw new IllegalArgumentException("예약 가능한 좌석이 없습니다.");
        }
    }
}
