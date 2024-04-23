package com.midsummra.subway.entity.neo4j;

import jakarta.persistence.SecondaryTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.*;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Station {

    @Id
//    @Version
    @GeneratedValue
    private Long id;

    private String stationName;

    private ArrayList<String> line;

    private boolean isBlocked = false;

    @Relationship(type = "path", direction = Relationship.Direction.OUTGOING)
    private Set<Edge> stationEdge;

    public void addEdge(Edge edge){
        if (stationEdge == null){
            this.stationEdge = new HashSet<>();
        }
        stationEdge.add(edge);
    }

    public void addLine(String lineName){
        if (this.line == null){
            this.line = new ArrayList<>();
        }
        this.line.add(lineName);
    }
}
