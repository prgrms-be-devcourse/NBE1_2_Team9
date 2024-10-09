package com.grepp.nbe1_2_team09.schedule.controller.dto;

import java.io.Serializable;

public record SelectionData(String date, String startTime, String endTime) implements Serializable {
}
