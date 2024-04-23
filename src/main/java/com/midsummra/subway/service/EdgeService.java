package com.midsummra.subway.service;

import com.midsummra.subway.entity.neo4j.Edge;

public interface EdgeService {

    Edge findEdge(String lineName, String StationAName, String StationBName);
}
