package com.grepp.nbe1_2_team09.domain.service.location;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.LocationException;
import com.grepp.nbe1_2_team09.domain.entity.Location;
import com.grepp.nbe1_2_team09.domain.repository.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    // 리뷰 조회 예외처리
    public Location findByIdOrThrowReviewException(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", locationId, ExceptionMessage.REVIEW_NOT_FOUND);
                    return new LocationException(ExceptionMessage.REVIEW_NOT_FOUND);
                });
    }
}
