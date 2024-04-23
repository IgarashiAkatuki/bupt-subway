package com.midsummra.subway.repository;

import com.midsummra.subway.entity.mysql.StationDistance;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface StationDistanceRepo extends CrudRepository<StationDistance, Integer> {

    @Modifying
    @Query(value = "update StationDistance sd set sd.time = sd.distance / :speed" +
            " where sd.line = :line")
    public int calculateTime(@Param("line") String line, @Param("speed") double speed);

    public ArrayList<StationDistance> findStationDistanceByStationAAndLine(String distanceA, String line);

    public ArrayList<StationDistance> findStationDistanceByStationBAndLine(String distanceA, String line);

    public StationDistance findStationDistanceByStationAAndStationBAndLine(String stationA, String stationB, String line);

    public ArrayList<StationDistance> findAllByStationAAndStationB(String stationA, String stationB);
}
