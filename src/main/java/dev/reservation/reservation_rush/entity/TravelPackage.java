package dev.reservation.reservation_rush.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import dev.reservation.reservation_rush.common.exception.BusinessException;
import dev.reservation.reservation_rush.common.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "travel_packages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TravelPackage extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("여행 패키지 ID")
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

    @Version
    @Column(name = "version", nullable = false)
    @Comment("버전")
    private Long version;

    public boolean isAvailable() {
        return availableSeats > 0;
    }

    public void decreaseSeats() {
        validateAvailableSeats();
        this.availableSeats--;
    }

    public void increaseSeats() {
        if (this.availableSeats >= this.totalSeats) {
            throw new BusinessException(ErrorCode.SEAT_OVERFLOW);
        }
        this.availableSeats++;
    }

    private void validateAvailableSeats() {
        if (this.availableSeats <= 0) {
            throw new BusinessException(ErrorCode.SOLD_OUT_PACKAGE);
        }
    }
}
