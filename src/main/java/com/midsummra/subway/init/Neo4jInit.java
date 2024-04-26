package com.midsummra.subway.init;

import com.midsummra.subway.entity.dto.GraphExist;
import com.midsummra.subway.repository.StationRepo;
import com.midsummra.subway.service.StationService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Neo4jInit {

    private final StationRepo stationRepo;

    private final StationService stationService;

    @Autowired
    public Neo4jInit(StationRepo stationRepo, StationService stationService) {
        this.stationRepo = stationRepo;
        this.stationService = stationService;
    }

    @PostConstruct
    public void init() {
        GraphExist test = stationRepo.graphExist("test");
        if (!test.isExists()){
            try {
                stationService.resetGraph();
            }catch (Exception e){
                System.out.println("graph初始化完成");
            }
        }
    }
}
