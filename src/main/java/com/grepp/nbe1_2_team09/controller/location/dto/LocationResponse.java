package com.grepp.nbe1_2_team09.controller.location.dto;

import com.grepp.nbe1_2_team09.domain.entity.LocationType;

public record LocationResponse (
        String placeId,
        String name,
        String photoUrl,
        LocationType type
) {}
