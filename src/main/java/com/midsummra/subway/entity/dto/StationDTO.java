package com.midsummra.subway.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StationDTO {


    private int id;

    private String lineName;

    private String stationName;

    private ArrayList<LocalTime> weekdayTimetableToStart;

    private ArrayList<LocalTime> weekdayTimetableToEnd;

    private ArrayList<LocalTime> weekendTimetableToStart;

    private ArrayList<LocalTime> weekendTimetableToEnd;
}
