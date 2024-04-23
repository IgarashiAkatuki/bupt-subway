package com.midsummra.subway.service;

import com.midsummra.subway.entity.mysql.Subway;
import com.midsummra.subway.entity.neo4j.Station;

import java.util.ArrayList;

public interface LineService {

    ArrayList<Station> deleteLine(String lineName);

    ArrayList<Station> recoverLine(String lineName);

    Subway findSubway(String lineName);

    ArrayList<Subway> findAllSubway();

    ArrayList<Station> addLine(String lineName, double speed, ArrayList<String> timetable,
                               ArrayList<String> stationNames, ArrayList<Double> distances)
            throws Exception;

    Subway addSubway(String lineName, double speed);
}
