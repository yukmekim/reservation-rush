package dev.reservation.reservation_rush.entity;

import org.hibernate.annotations.Comment;

import dev.reservation.reservation_rush.enums.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Booking extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_package_id", nullable = false)
    @Comment("여행 패키지 ID")
    private TravelPackage travelPackageId;
    
    @Column(name = "user_id", nullable = false)
    @Comment("사용자ID")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("예약 상태")
    private BookingStatus status;

    public void confirm() {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("이미 취소된 예약은 확정할 수 없습니다.");
        }
        this.status = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("이미 취소된 예약입니다.");
        }
        this.status = BookingStatus.CANCELLED;
    }
}
