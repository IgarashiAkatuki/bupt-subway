package com.midsummra.subway.entity.vo;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineVO {

    public LineVO(String lineName) {
        this.lineName = lineName;
    }

    @NotNull
    private String lineName;

    private double speed;

    // json
    private String stations;

    //json
    private String distances;

    //json
    private String timetables;
}
