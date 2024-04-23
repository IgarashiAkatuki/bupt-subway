package com.midsummra.subway.entity.dto;

import com.midsummra.subway.entity.neo4j.Station;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StationPath {

    public StationPath(KShortestPathDTO kShortestPathDTO) {
        this.sourceNodeName = kShortestPathDTO.getSourceNodeName();
        this.targetNodeName = kShortestPathDTO.getTargetNodeName();
        this.totalCost = kShortestPathDTO.getTotalCost();
        this.nodeNames = kShortestPathDTO.getNodeNames();
        this.costs = kShortestPathDTO.getCosts();
        this.path = kShortestPathDTO.getPath();
    }

    private String sourceNodeName;

    private String targetNodeName;

    private Double totalCost;

    private ArrayList<String> nodeNames;

    private ArrayList<Double> costs;

    private ArrayList<Station> path;

    private int exchanges;

    private LinkedHashMap<String, ArrayList<String>> exchangeStation;

    private LinkedHashMap<String, LocalTime> exchangeTime;

    private LocalTime startTime;

    private LocalTime endTime;

    private Double totalDistance = 0.0;
}
