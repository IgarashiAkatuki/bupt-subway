package com.midsummra.subway.entity.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RelationshipProperties()
public class Edge {

    @RelationshipId
    @GeneratedValue
    private Long id;

    private Double distance;

    private String line;

    private Double time;

    private String stationA;

    private String stationB;

    @TargetNode
    private Station targetStation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(id, edge.id) && Objects.equals(distance, edge.distance) && Objects.equals(line, edge.line) && Objects.equals(time, edge.time) && Objects.equals(stationA, edge.stationA) && Objects.equals(stationB, edge.stationB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, distance, line, time, stationA, stationB);
    }
}
