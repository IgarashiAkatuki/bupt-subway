package com.midsummra.subway.entity.dto;

import com.midsummra.subway.entity.neo4j.Station;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KShortestPathDTO {

    private int index;

    private String sourceNodeName;

    private String targetNodeName;

    private Double totalCost;

    private ArrayList<String> nodeNames;

    private ArrayList<Double> costs;

    private ArrayList<Station> path;

}
