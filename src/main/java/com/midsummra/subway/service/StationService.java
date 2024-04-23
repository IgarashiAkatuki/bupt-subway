package com.midsummra.subway.service;

import com.midsummra.subway.entity.dto.KShortestPathDTO;
import com.midsummra.subway.entity.dto.StationPath;
import com.midsummra.subway.entity.mysql.Station;
import com.midsummra.subway.entity.mysql.StationDistance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public interface StationService {

    ArrayList<StationDistance> findStationDistanceByFrom(String stationName, String lineName);

    ArrayList<StationDistance> findStationDistanceByTo(String stationName, String lineName);

    ArrayList<StationDistance> findStationDistance(String stationA, String stationB);

    Station saveStation(Station station);

    Station findStation(String stationName);

    Station findStation(String stationName, String lineName);

    StationDistance findStationDistance(String stationAName, String stationBName, String lineName);

    ArrayList<KShortestPathDTO> findKShortestPath(String stationAName, String stationBName, int k);

    ArrayList<KShortestPathDTO> findKShortestPath(String stationAName, String stationBName);

    ArrayList<StationPath> calculatePath(ArrayList<KShortestPathDTO> kShortestPathDTOS) throws Exception;

    ArrayList<StationPath> calculatePath(ArrayList<KShortestPathDTO> kShortestPathDTOS, LocalTime time, boolean isWeekend) throws Exception;

    com.midsummra.subway.entity.neo4j.Station blockStation(String stationName);

    com.midsummra.subway.entity.neo4j.Station unBlockStation(String stationName);

    ArrayList<com.midsummra.subway.entity.neo4j.Station> findAllStations(boolean isBlocked);

    boolean resetGraph();

}
