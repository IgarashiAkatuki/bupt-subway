package com.midsummra.subway.entity.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StationDistance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "station_a")
    private String stationA;

    @Column(name = "station_b")
    private String stationB;

    private Double distance;

    private String line;

    private boolean isBlocked;

    private Double time;
}
