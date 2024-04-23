package com.midsummra.subway.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GDSProject {

    private String nodeProjection;

    private String relationshipProjection;

    private String graphName;

    private int nodeCount;

    private int relationshipCount;

    private long projectMillis;
}

