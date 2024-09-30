package com.grepp.nbe1_2_team09.domain.service.location;

import com.grepp.nbe1_2_team09.controller.location.dto.CreateLocationRequest;
import com.grepp.nbe1_2_team09.controller.location.dto.LocationDto;
import com.grepp.nbe1_2_team09.domain.entity.Location;
import com.grepp.nbe1_2_team09.domain.entity.LocationType;
import com.grepp.nbe1_2_team09.domain.repository.location.LocationRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LocationServiceTest {

    @Autowired
    private LocationService locationService;
    @Autowired
    private LocationRepository locationRepository;


    @AfterEach
    public void after(){
       //
    }

    @BeforeEach
    public void before(){
       //
    }

    @Test
    @DisplayName("장소 저장 성공")
    void createLocation(){
        //given
        CreateLocationRequest locationReq = new CreateLocationRequest(
                "남산타워",
                new BigDecimal("37.5511694"),
                new BigDecimal("126.9882266"),
                "105 Namsangongwon-gil," +" Yongsan-gu",
                new BigDecimal(4),
                LocationType.RESTAURANT,
                "dd"
        );

        //when
         LocationDto savedLocation = locationService.saveLocation(locationReq);

        //then
        assertNotNull(savedLocation);
        assertEquals(savedLocation.placeName(), locationReq.placeName());
        assertEquals(savedLocation.latitude(), locationReq.latitude());
        assertEquals(savedLocation.longitude(), locationReq.longitude());
        assertEquals(savedLocation.address(), locationReq.address());
        assertEquals(savedLocation.type(), locationReq.type());
        assertEquals(savedLocation.photo(),locationReq.photo());
    }

    @Test
    @DisplayName("장소 조회 성공")
    void getLocationById(){
        //given
        Location location = locationRepository.save(
                Location.builder()
                        .placeName("남산타워")
                        .latitude(BigDecimal.valueOf(37.5511694))
                        .longitude(BigDecimal.valueOf(126.9882266))
                        .address("105 Namsangongwon-gil," +" Yongsan-gu")
                        .rating(BigDecimal.valueOf(4))
                        .type(LocationType.RESTAURANT)
                        .build());

        //when
        LocationDto result = locationService.getLocationById(location.getLocationId());

        //then
        assertNotNull(result);
        assertEquals(location.getLocationId(), result.locationId());
        assertEquals(location.getPlaceName(), result.placeName());
        assertEquals(location.getLatitude(), result.latitude());
        assertEquals(location.getLongitude(), result.longitude());
        assertEquals(location.getAddress(), result.address());
        assertEquals(location.getRating(), result.rating());
        assertEquals(location.getType(), result.type());
    }

    @Test
    @DisplayName("장소 삭제 성공")
    void deleteLocation() {
        // given
        Location location = locationRepository.save(
                Location.builder()
                        .placeName("남산타워")
                        .latitude(BigDecimal.valueOf(37.5511694))
                        .longitude(BigDecimal.valueOf(126.9882266))
                        .address("105 Namsangongwon-gil," +" Yongsan-gu")
                        .rating(BigDecimal.valueOf(4))
                        .type(LocationType.RESTAURANT)
                        .build());

        // when
        locationService.deleteLocationById(location.getLocationId());

        // then
        assertFalse(locationRepository.existsById(location.getLocationId()));
    }



}