package com.midsummra.subway.controller;

import com.midsummra.subway.common.response.Result;
import com.midsummra.subway.common.response.StatusCode;
import com.midsummra.subway.entity.mysql.Station;
import com.midsummra.subway.entity.vo.StationVO;
import com.midsummra.subway.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController()
@RequestMapping("/api/station")
@CrossOrigin
public class StationController {

    private final StationService stationService;

    @Autowired
    public StationController(StationService stationService) {
        this.stationService = stationService;
    }


    @GetMapping("/blockStation")
    public Result blockStation(StationVO stationVO){
        String stationName = stationVO.getStationName();
        if (stationName == null || stationName.isEmpty()){
            return Result.error(StatusCode.INVALID_PARAMETERS.getStatusCode(),
                    StatusCode.INVALID_PARAMETERS.getResultMessage());
        }
        Station station = stationService.findStation(stationName);
        if (station == null){
            return Result.error(StatusCode.INVALID_PARAMETERS.getStatusCode(),
                    StatusCode.INVALID_PARAMETERS.getResultMessage());
        }
        com.midsummra.subway.entity.neo4j.Station blockStation = stationService.blockStation(stationName);
        return Result.succeed(blockStation);
    }

    @GetMapping("/unblockStation")
    public Result unblockStation(String stationName){
        if (stationName == null || stationName.isEmpty()){
            return Result.error(StatusCode.INVALID_PARAMETERS.getStatusCode(),
                    StatusCode.INVALID_PARAMETERS.getResultMessage());
        }
        Station station = stationService.findStation(stationName);
        if (station == null){
            return Result.error(StatusCode.INVALID_PARAMETERS.getStatusCode(),
                    StatusCode.INVALID_PARAMETERS.getResultMessage());
        }
        com.midsummra.subway.entity.neo4j.Station unblockStation = stationService.unBlockStation(stationName);
        return Result.succeed(unblockStation);
    }

    @GetMapping("/findAllBlockedStation")
    public Result findAllBlockedStation(){
        ArrayList<com.midsummra.subway.entity.neo4j.Station> allStations = stationService.findAllStations(true);
        return Result.succeed(allStations);
    }

    @GetMapping("/findAllStation")
    public Result findAllStation(){
        ArrayList<com.midsummra.subway.entity.neo4j.Station> allStations = stationService.findAllStations(false);
        return Result.succeed(allStations);
    }

    @GetMapping("/refreshStation")
    public Result resetStation(){
        stationService.resetGraph();
        return Result.succeed("刷新成功");
    }
}
