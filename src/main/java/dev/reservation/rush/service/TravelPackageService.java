package dev.reservation.rush.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.reservation.rush.common.exception.BusinessException;
import dev.reservation.rush.common.exception.ErrorCode;
import dev.reservation.rush.common.response.PageResponse;
import dev.reservation.rush.dto.request.TravelPackageCreateRequest;
import dev.reservation.rush.dto.response.TravelPackageResponse;
import dev.reservation.rush.entity.TravelPackage;
import dev.reservation.rush.repository.TravelPackageRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPackageService {
    private final TravelPackageRepository travelPackageRepository;

    // 여행 패키지 생성
    @Transactional
    public TravelPackageResponse createPackage(TravelPackageCreateRequest request) {
        TravelPackage travelPackage = request.toEntity();

        return TravelPackageResponse.from(travelPackageRepository.save(travelPackage));
    }

    // 특정 여행 패키지 조회
    public TravelPackageResponse getPackage(Long id) {
        TravelPackage travelPackage = travelPackageRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PACKAGE_NOT_FOUND));

        return TravelPackageResponse.from(travelPackage);
    }

    // 여행 패키지 목록 조회
    public PageResponse<TravelPackageResponse> getPackages(Pageable pageable) {
        Page<TravelPackage> travelPackages = travelPackageRepository.findAll(pageable);
        return PageResponse.from(travelPackages, TravelPackageResponse::from);
    }
}
