package com.midsummra.subway.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.midsummra.subway.common.response.Result;
import com.midsummra.subway.common.response.StatusCode;
import com.midsummra.subway.entity.dto.KShortestPathDTO;
import com.midsummra.subway.entity.dto.StationPath;
import com.midsummra.subway.entity.mysql.Station;
import com.midsummra.subway.entity.mysql.Subway;
import com.midsummra.subway.entity.vo.LineVO;
import com.midsummra.subway.entity.vo.PathVO;
import com.midsummra.subway.service.LineService;
import com.midsummra.subway.service.LineServiceImpl;
import com.midsummra.subway.service.StationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;

@RestController()
@RequestMapping("/api/path")
@CrossOrigin
public class PathController {

    private final StationService stationService;

    private final LineService lineService;

    @Autowired
    public PathController(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    @GetMapping("/getPath")
    public Result getPath(@Valid PathVO pathVO){
        if ((pathVO.getStationA().trim().isEmpty()|| pathVO.getStationB().trim().isEmpty())){
            return Result.error(StatusCode.INVALID_PARAMETERS.getStatusCode(), "地铁站不存在或封闭");
        }
        Station stationA = stationService.findStation(pathVO.getStationA());
        Station stationB = stationService.findStation(pathVO.getStationB());
        if (stationA == null || stationB == null) {
            return Result.error(StatusCode.INVALID_PARAMETERS.getStatusCode(), "地铁站不存在或封闭");
        }
        String[] split = pathVO.getCurrentTime().split(":");
        LocalTime localTime = LocalTime.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        boolean isWeekend = "Sat".equals(pathVO.getCurrentDate()) || "Sun".equals(pathVO.getCurrentDate());
        ArrayList<KShortestPathDTO> kShortestPath = stationService.findKShortestPath(pathVO.getStationA(), pathVO.getStationB());
        if (kShortestPath == null || kShortestPath.isEmpty()){
            return Result.error(StatusCode.NOT_FOUND.getStatusCode(), "不存在此线路");
        }

        ArrayList<StationPath> stationPaths = null;
        try {
            stationPaths = stationService.calculatePath(kShortestPath, localTime, isWeekend);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ArrayList<StationPath> resultPath = new ArrayList<>();
        stationPaths.sort((a, b) -> {
            return (int) (a.getTotalCost() - b.getTotalCost());
        });
        resultPath.add(stationPaths.get(0));
        stationPaths.sort((a, b) -> {
            return a.getExchanges() - b.getExchanges();
        });
        resultPath.add(stationPaths.get(0));

        return Result.succeed(resultPath);
    }

    @GetMapping("/deleteLine")
    public Result deleteLine(LineVO lineVO){
        String lineName = lineVO.getLineName();
        System.out.println(lineName);
        if (lineName == null || lineName.trim().isEmpty()){
            return Result.error(StatusCode.NOT_FOUND.getStatusCode(), StatusCode.NOT_FOUND.getResultMessage());
        }
        Subway subway = lineService.findSubway(lineName);
        if (subway == null){
            return Result.error(StatusCode.NOT_FOUND.getStatusCode(), StatusCode.NOT_FOUND.getResultMessage());
        }
        ArrayList<com.midsummra.subway.entity.neo4j.Station> stations = lineService.deleteLine(lineName);
        return Result.succeed(stations);
    }

    @GetMapping("/recoverLine")
    public Result recoverLine(LineVO lineVO){
        String lineName = lineVO.getLineName();
        if (lineName == null || lineName.trim().isEmpty()){
            return Result.error(StatusCode.NOT_FOUND.getStatusCode(), StatusCode.NOT_FOUND.getResultMessage());
        }
        Subway subway = lineService.findSubway(lineName);
        if (subway == null){
            return Result.error(StatusCode.NOT_FOUND.getStatusCode(), StatusCode.NOT_FOUND.getResultMessage());
        }
        ArrayList<com.midsummra.subway.entity.neo4j.Station> stations = lineService.recoverLine(lineName);
        return Result.succeed(stations);
    }

    @PostMapping("/addNewLine")
    public Result addLine(LineVO lineVO) throws Exception{
        if (lineVO.getLineName() == null || lineVO.getLineName().trim().isEmpty()){
            return Result.error(StatusCode.NOT_FOUND.getStatusCode(), StatusCode.NOT_FOUND.getResultMessage());
        }
        if (lineVO.getDistances() == null || lineVO.getDistances().trim().isEmpty()){
            return Result.error(StatusCode.NOT_FOUND.getStatusCode(), StatusCode.NOT_FOUND.getResultMessage());
        }
        if (lineVO.getTimetables() == null || lineVO.getTimetables().trim().isEmpty()){
            return Result.error(StatusCode.NOT_FOUND.getStatusCode(), StatusCode.NOT_FOUND.getResultMessage());
        }
        if (lineVO.getStations() == null || lineVO.getStations().trim().isEmpty()){
            return Result.error(StatusCode.NOT_FOUND.getStatusCode(), StatusCode.NOT_FOUND.getResultMessage());
        }

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> stations = mapper.readValue(lineVO.getStations(), new TypeReference<ArrayList<String>>() {
        });
        ArrayList<String> timetables = mapper.readValue(lineVO.getTimetables(), new TypeReference<ArrayList<String>>() {
        });
        ArrayList<Double> distances = mapper.readValue(lineVO.getDistances(), new TypeReference<ArrayList<Double>>() {});

        return Result.succeed(timetables);
    }

    @GetMapping("/getAllLine")
    public Result getAllLine(){
        ArrayList<Subway> allSubway = lineService.findAllSubway();
        return Result.succeed(allSubway);
    }
}
