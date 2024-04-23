package com.midsummra.subway.common.utils;

import com.midsummra.subway.entity.neo4j.Edge;
import com.midsummra.subway.entity.neo4j.Station;
import com.midsummra.subway.repository.EdgeRepo;
import com.midsummra.subway.repository.StationDistanceRepo;
import com.midsummra.subway.repository.StationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Neo4jUtils {

    private final StationRepo stationRepo;

    private final EdgeRepo edgeRepo;

    private final StationDistanceRepo stationDistanceRepo;

    @Autowired
    public Neo4jUtils(StationRepo stationRepo, EdgeRepo edgeRepo, StationDistanceRepo stationDistanceRepo) {
        this.stationRepo = stationRepo;
        this.edgeRepo = edgeRepo;
        this.stationDistanceRepo = stationDistanceRepo;
    }


    public void loadGraph(){

    }

    private void loadNodes(){
        stationDistanceRepo.findAll().forEach(stationDistance -> {
            String stationA = stationDistance.getStationA();
            String stationB = stationDistance.getStationB();
            Station station = stationRepo.findAllByStationName(stationA);
            if (station == null){
                station = new Station();
                station.setStationName(stationA);
                station.setBlocked(false);
                station.addLine(stationDistance.getLine());
                stationRepo.save(station);
            }else {
                if (!station.getLine().contains(stationDistance.getLine())){
                    station.addLine(stationDistance.getLine());
                    stationRepo.save(station);
                }
            }


            station = stationRepo.findAllByStationName(stationB);
            if (station == null){
                station = new Station();
                station.setStationName(stationB);
                station.setBlocked(false);
                station.addLine(stationDistance.getLine());
                stationRepo.save(station);
            }else {
                if (!station.getLine().contains(stationDistance.getLine())){
                    station.addLine(stationDistance.getLine());
                    stationRepo.save(station);
                }
            }

        });
    }

    private void loadEdges(){
        stationDistanceRepo.findAll().forEach(stationDistance -> {
            String stationA = stationDistance.getStationA();
            String stationB = stationDistance.getStationB();
            Edge edge = edgeRepo.findAllByLineAndStationAAndStationB(stationDistance.getLine(),
                    stationA, stationB);
            if (edge != null){
                return;
            }
            edge = new Edge();
            edge.setDistance(stationDistance.getDistance());
            edge.setLine(stationDistance.getLine());
            edge.setTime(stationDistance.getTime());
            edge.setStationA(stationA);
            edge.setStationB(stationB);
            Station station1 = stationRepo.findAllByStationName(stationA);
            Station station2 = stationRepo.findAllByStationName(stationB);
            station1.addEdge(edge);
            edge.setTargetStation(station2);

            stationRepo.save(station1);
            stationRepo.save(station2);
        });
    }

}
