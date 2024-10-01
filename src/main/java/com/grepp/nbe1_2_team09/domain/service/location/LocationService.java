package com.grepp.nbe1_2_team09.domain.service.location;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.LocationException;
import com.grepp.nbe1_2_team09.controller.location.dto.CreateLocationRequest;
import com.grepp.nbe1_2_team09.controller.location.dto.LocationDto;
import com.grepp.nbe1_2_team09.domain.entity.Location;
import com.grepp.nbe1_2_team09.domain.repository.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;

    //장소 저장
    @Transactional
    public LocationDto saveLocation(CreateLocationRequest locationReq) {
        //DTO->ENTITY
        Location savedLocation = locationRepository.save(locationReq.toEntity());
        return LocationDto.fromEntity(savedLocation);
    }

    //장소 조회
    @Transactional
    public LocationDto getLocationById(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationException(ExceptionMessage.LOCATION_NOT_FOUND));
        return LocationDto.fromEntity(location);
    }

    //장소 삭제
    @Transactional
    public void deleteLocationById(Long locationId) {
       locationRepository.deleteById(locationId);

    }

   /* //장소 수정
    @Transactional
    public Location updateLocation(Long locationId,Location location) {
        Location location1 = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationException(ExceptionMessage.LOCATION_NOT_FOUND));

    }*/

}
