package dev.reservation.rush.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.reservation.rush.common.response.PageResponse;
import dev.reservation.rush.common.response.Response;
import dev.reservation.rush.dto.request.TravelPackageCreateRequest;
import dev.reservation.rush.dto.response.TravelPackageResponse;
import dev.reservation.rush.service.TravelPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
@Tag(name = "여행 패키지", description = "여행 패키지 관리 API")
public class TravelPackageController {
    
    private final TravelPackageService travelPackageService;

    @PostMapping
    @Operation(summary = "여행 패키지 생성", description = "새로운 여행 패키지를 생성합니다.")
    public ResponseEntity<Response<TravelPackageResponse>> createPackage(@Valid @RequestBody TravelPackageCreateRequest request) {
        TravelPackageResponse response = travelPackageService.createPackage(request);
        return ResponseEntity.ok(Response.success(response, "여행 패키지 생성에 성공했습니다."));
    }

    @GetMapping("/{id}")
    @Operation(summary = "여행 패키지 조회", description = "ID로 특정 여행 패키지를  조회합니다.")
    public ResponseEntity<Response<TravelPackageResponse>> getPackages(@PathVariable Long id) {
        TravelPackageResponse response = travelPackageService.getPackage(id);
        return ResponseEntity.ok(Response.success(response, "여행 패키지 조회에 성공했습니다."));
    }

    @GetMapping
    @Operation(summary = "여행 패키지 목록 조회", description = "페이징된 여행 패키지 목록을 조회합니다.")
    public ResponseEntity<Response<PageResponse<TravelPackageResponse>>> getPackages(
        @Parameter(hidden = true)
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<TravelPackageResponse> response = travelPackageService.getPackages(pageable);
        return ResponseEntity.ok(Response.success(response, "여행 패키지 목록 조회에 성공했습니다."));
    }
}
