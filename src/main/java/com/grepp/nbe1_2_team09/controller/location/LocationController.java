package com.grepp.nbe1_2_team09.controller.location;

import com.grepp.nbe1_2_team09.domain.service.Location.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LocationController {

    private final LocationService locationService;

}
