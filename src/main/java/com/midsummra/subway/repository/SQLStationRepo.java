package com.midsummra.subway.repository;

import com.midsummra.subway.entity.mysql.Station;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface SQLStationRepo extends CrudRepository<Station, Integer> {

//    Station findAllByStationName(String stationName);

    Station findAllByStationNameAndLine(String stationName, String line);

    ArrayList<Station> findAllByStationName(String stationName);
}
