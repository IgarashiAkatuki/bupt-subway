package com.midsummra.subway.repository;

import com.midsummra.subway.entity.neo4j.Edge;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EdgeRepo extends Neo4jRepository<Edge, Long> {

    Edge findAllByLineAndStationAAndStationB(String linea, String stationA, String stationB);

}
