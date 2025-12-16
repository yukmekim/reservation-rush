package dev.reservation.reservation_rush.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.reservation.reservation_rush.common.response.Response;
import dev.reservation.reservation_rush.dto.request.BookingCreateRequest;
import dev.reservation.reservation_rush.dto.response.BookingResponse;
import dev.reservation.reservation_rush.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "예약", description = "예약 관리 API")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "예약 생성", description = "새로운 예약을 생성합니다.")
    public ResponseEntity<Response<BookingResponse>> createBooking(@Valid @RequestBody BookingCreateRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.ok(Response.success(response, "예약에 성공했습니다."));
    }

    @GetMapping
    @Operation(summary = "예약 목록 조회", description = "해당 패키지에 대한 예약 목록을 조회합니다.")
    public ResponseEntity<Response<List<BookingResponse>>> getBookings(Long packageId) {
        List<BookingResponse> response = bookingService.getBookings(packageId);
        return ResponseEntity.ok(Response.success(response, "예약 목록 조회에 성공했습니다."));
    }
}
