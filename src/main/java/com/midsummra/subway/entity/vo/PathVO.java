package com.midsummra.subway.entity.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class PathVO {

    @NotNull
    private String stationA;

    @NotNull
    private String stationB;

    @NotNull
    private String currentTime;

    @NotNull
    private String currentDate;
}
