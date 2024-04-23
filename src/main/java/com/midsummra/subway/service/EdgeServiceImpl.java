package com.midsummra.subway.service;

import com.midsummra.subway.entity.neo4j.Edge;
import com.midsummra.subway.repository.EdgeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EdgeServiceImpl implements EdgeService{

    private final EdgeRepo edgeRepo;

    @Autowired
    public EdgeServiceImpl(EdgeRepo edgeRepo) {
        this.edgeRepo = edgeRepo;
    }

    @Override
    public Edge findEdge(String lineName, String stationAName, String stationBName) {
        Edge station = edgeRepo.findAllByLineAndStationAAndStationB(lineName, stationAName, stationBName);
        if (station == null){
            station = edgeRepo.findAllByLineAndStationAAndStationB(lineName, stationBName, stationAName);
        }
        return station;
    }
}
