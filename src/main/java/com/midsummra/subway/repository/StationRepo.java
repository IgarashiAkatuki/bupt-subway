package com.midsummra.subway.repository;

import com.midsummra.subway.entity.dto.GDSProject;
import com.midsummra.subway.entity.dto.GraphExist;
import com.midsummra.subway.entity.dto.KShortestPathDTO;
import com.midsummra.subway.entity.neo4j.Edge;
import com.midsummra.subway.entity.neo4j.Station;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;


@Repository
public interface StationRepo extends Neo4jRepository<Station, Long> {

    Station findAllByStationName(String stationName);

    @Query("Match (source:Station{stationName: $stationA, isBlocked: false}), (target:Station{stationName: $stationB, isBlocked: false})\n" +
            "CALL gds.shortestPath.yens.stream('test', {\n" +
            "    sourceNode: source,\n" +
            "    targetNode: target,\n" +
            "    k: $k,\n" +
            "    relationshipWeightProperty: 'time'\n" +
            "})\n" +
            "YIELD index, sourceNode, targetNode, totalCost, nodeIds, costs, path\n" +
            "WHERE all(nodeId IN nodeIds WHERE NOT gds.util.asNode(nodeId).isBlocked)\n" +
            "RETURN\n" +
            "    index,\n" +
            "    gds.util.asNode(sourceNode).stationName AS sourceNodeName,\n" +
            "    gds.util.asNode(targetNode).stationName AS targetNodeName,\n" +
            "    totalCost,\n" +
            "    [nodeId IN nodeIds | gds.util.asNode(nodeId).stationName] AS nodeNames,\n" +
            "    costs,\n" +
            "    nodes(path) as path\n" +
            "ORDER BY index")
    ArrayList<KShortestPathDTO> findKShortestPath(@Param("stationA") String stationA, @Param("stationB") String stationB, @Param("k") int k);

    @Query("call gds.graph.project('test',\n" +
            "'Station',\n" +
            "{path:{properties:\"time\",orientation: 'UNDIRECTED'}})")
    GDSProject generateProjectGraph();

    @Query("MATCH (p:Station {stationName: $stationName}) SET p.isBlocked = $isBlocked return p")
    Station updateIsBlockedByStationName(@Param("stationName") String stationName, @Param("isBlocked") boolean isBlocked);

    @Query("MATCH (n:Station) \n" +
            "WHERE n.isBlocked = $isBlocked \n" +
            "RETURN n")
    ArrayList<Station> findAllByIsBlocked(@Param("isBlocked") boolean isBlocked);

    @Query("MATCH ()-[r]-()\n" +
            "DELETE r\n")
    ArrayList<Edge> deleteAllRelationShips();

    @Query("call gds.graph.exists( $graphName )\n")
    GraphExist graphExist(@Param("graphName") String graphName);

    @Query("call gds.graph.drop( $graphName )")
    void deleteGraph(@Param("graphName") String graphName);

    @Query("MATCH (n:Station) WHERE $lineName in n.line set n.isBlocked = true RETURN n")
    ArrayList<Station> deleteLine(@Param("lineName") String lineName);

    @Query("MATCH (n:Station) WHERE $lineName in n.line set n.isBlocked = false RETURN n")
    ArrayList<Station> recoverLine(@Param("lineName") String lineName);
}
