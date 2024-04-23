package com.midsummra.subway.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.midsummra.subway.entity.dto.Timetable;
import com.midsummra.subway.entity.dto.Timetables;
import com.midsummra.subway.entity.mysql.Station;
import com.midsummra.subway.entity.mysql.StationDistance;
import com.midsummra.subway.repository.SQLStationRepo;
import com.midsummra.subway.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;

@Component
public class StationUtils {

    private final SQLStationRepo stationRepo;

    private final StationService stationService;

    @Autowired
    public StationUtils(SQLStationRepo stationRepo, StationService stationService) {
        this.stationRepo = stationRepo;
        this.stationService = stationService;
    }

    public Timetables deserializationTimetables(String timetables) throws Exception{
        if (timetables == null || timetables.equals("")) {
            return new Timetables();
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
//        System.out.println(timetables == null);
        return mapper.readValue(timetables, new TypeReference<Timetables>() {
        });

    }

    public Station addStation(String stationName, String lineName){
        return addStation(stationName, lineName, null, null);
    }

    public Station addStation(String stationName, String lineName, Timetables weekday, Timetables weekend){
        Station station = new Station(1, stationName, null, null, lineName, weekday.toString(), weekend.toString());
        return stationRepo.save(station);
    }

    public void readTimetable(String timetableName, String lineName) throws Exception {
        String property = System.getProperty("user.dir");
        BufferedReader fileReader = new BufferedReader(
                new FileReader(new File(property + "/src/main/resources/timetable/" + timetableName)));

        String stationInfo = fileReader.readLine();
        String[] stationInfoArray = stationInfo.split("->");
        Station station = stationService.findStation(stationInfoArray[0], lineName);
        int distance = 0;
        String nextStationName = station.getNextStationName();
        if (nextStationName != null && !nextStationName.isEmpty()) {
            distance = 1;
        }

        Timetables timetablesWeekday = null;
        String weekdayTimetable = station.getWeekdayTimetable();
        if (weekdayTimetable == null || weekdayTimetable.isEmpty()) {
            timetablesWeekday = new Timetables();
            timetablesWeekday.setDay("weekday");
        } else {
            timetablesWeekday = deserializationTimetables(weekdayTimetable);
        }
        fileReader.readLine();

        ArrayList<LocalTime> list = new ArrayList<>();
        Timetable timetable = new Timetable();
        timetable.setTimetable(list);
        timetable.setFrom(station.getStationName());
        if (distance == 0) {
            timetable.setTo(station.getPrevStationName());
        } else {
            timetable.setTo(station.getNextStationName());
        }

        String times = null;
        while (!(times = fileReader.readLine()).isEmpty()) {
            times = times.trim();
            String[] split = times.split(" ");
            int hour = Integer.parseInt(split[0]);
            for (int i = 1; i < split.length; i++) {
                list.add(LocalTime.of(hour, Integer.parseInt(split[i])));
            }
        }
        if (timetablesWeekday.getTimetableA() == null) {
            timetablesWeekday.setTimetableA(timetable);
        } else {
            timetablesWeekday.setTimetableB(timetable);
        }
        fileReader.readLine();

        Timetables timetablesWeekend = null;
        String weekendTimetable = station.getWeekendTimetable();
        if (weekendTimetable == null || weekendTimetable.isEmpty()) {
            timetablesWeekend = new Timetables();
            timetablesWeekend.setDay("weekend");
        } else {
            timetablesWeekend = deserializationTimetables(weekendTimetable);
        }

        ArrayList<LocalTime> list1 = new ArrayList<>();
        Timetable timetable1 = new Timetable();
        timetable1.setTimetable(list1);
        timetable1.setFrom(station.getStationName());
        if (distance == 0) {
            timetable1.setTo(station.getPrevStationName());
        } else {
            timetable1.setTo(station.getNextStationName());
        }

        while (!(times = fileReader.readLine()).isEmpty()) {
            times = times.trim();
            String[] split = times.split(" ");
            int hour = Integer.parseInt(split[0]);
            for (int i = 1; i < split.length; i++) {
                list1.add(LocalTime.of(hour, Integer.parseInt(split[i])));
            }
        }
        if (timetablesWeekend.getTimetableA() == null) {
            timetablesWeekend.setTimetableA(timetable1);
        } else {
            timetablesWeekend.setTimetableB(timetable1);
        }

        station.setWeekdayTimetable(timetablesWeekday.toString());
        station.setWeekendTimetable(timetablesWeekend.toString());
        stationService.saveStation(station);
//        System.out.println(station);

        boolean haveNextStation = true;
        while (haveNextStation) {
            String prevStationName = null;
            nextStationName = null;
            if (distance == 1) {
                station = stationService.findStation(station.getNextStationName(), lineName);
                prevStationName = station.getPrevStationName();
                nextStationName = station.getNextStationName();
            } else {
                station = stationService.findStation(station.getPrevStationName(), lineName);
                prevStationName = station.getNextStationName();
                nextStationName = station.getPrevStationName();
            }
            if (nextStationName == null || station.getStationName().equals("巴沟")) {
                haveNextStation = false;
            }
//            System.out.println(prevStationName);

            Station prevStation = stationService.findStation(prevStationName, lineName);
            String prevStationWeekdayTimetableStr = prevStation.getWeekdayTimetable();
            String prevStationWeekendTimetableStr = prevStation.getWeekendTimetable();

            String currentStationWeekdayTimetableStr = station.getWeekdayTimetable();
            String currentStationWeekendTimetableStr = station.getWeekendTimetable();

            Timetables prevStationWeekdayTimetable = deserializationTimetables(prevStationWeekdayTimetableStr);
            Timetables prevStationWeekendTimetable = deserializationTimetables(prevStationWeekendTimetableStr);

            Timetables currentStationWeekdayTimetable = new Timetables();
            currentStationWeekdayTimetable.setDay("weekday");
            if (currentStationWeekdayTimetableStr != null && !currentStationWeekdayTimetableStr.isEmpty()) {
                currentStationWeekdayTimetable = deserializationTimetables(currentStationWeekdayTimetableStr);
            }
            Timetables currentStationWeekendTimetable = new Timetables();
            currentStationWeekendTimetable.setDay("weekend");
            if (currentStationWeekendTimetableStr != null && !currentStationWeekendTimetableStr.isEmpty()) {
                currentStationWeekendTimetable = deserializationTimetables(currentStationWeekendTimetableStr);
            }
            StationDistance stationDistance = stationService.findStationDistance(station.getStationName(), prevStationName, lineName);
            Double time = stationDistance.getTime();
            System.out.println(time);

            Timetable currentWeekdayTimetable = new Timetable();
            currentWeekdayTimetable.setFrom(station.getStationName());
            currentWeekdayTimetable.setTo(nextStationName);
            ArrayList<LocalTime> list2 = new ArrayList<>();
            currentWeekdayTimetable.setTimetable(list2);

            if (prevStationWeekdayTimetable.getTimetableB() == null) {
                prevStationWeekdayTimetable.getTimetableA().getTimetable().forEach(localTime -> {
                    LocalTime tempTime = localTime.plusSeconds(time.longValue()).plusMinutes(1);
                    list2.add(tempTime);
                });
            } else {
                prevStationWeekdayTimetable.getTimetableB().getTimetable().forEach(localTime -> {
                    LocalTime tempTime = localTime.plusSeconds(time.longValue()).plusMinutes(1);
                    list2.add(tempTime);
                });
            }

            Timetable currentWeekendTimetable = new Timetable();
            currentWeekendTimetable.setFrom(station.getStationName());
            currentWeekendTimetable.setTo(nextStationName);
            ArrayList<LocalTime> list3 = new ArrayList<>();
            currentWeekendTimetable.setTimetable(list3);

            if (prevStationWeekendTimetable.getTimetableB() == null){
                prevStationWeekendTimetable.getTimetableA().getTimetable().forEach(localTime -> {
                    LocalTime tempTime = localTime.plusSeconds(time.longValue()).plusMinutes(1);
                    list3.add(tempTime);
                });
            }else {
                prevStationWeekendTimetable.getTimetableB().getTimetable().forEach(localTime -> {
                    LocalTime tempTime = localTime.plusSeconds(time.longValue()).plusMinutes(1);
                    list3.add(tempTime);
                });
            }

            if (currentStationWeekdayTimetable.getTimetableA() == null){
                currentStationWeekdayTimetable.setTimetableA(currentWeekdayTimetable);
            }else {
                currentStationWeekdayTimetable.setTimetableB(currentWeekdayTimetable);
            }
            if (currentStationWeekendTimetable.getTimetableA() == null){
                currentStationWeekendTimetable.setTimetableA(currentWeekendTimetable);
            }else {
                currentStationWeekendTimetable.setTimetableB(currentWeekendTimetable);
            }

            station.setWeekdayTimetable(currentStationWeekdayTimetable.toString());
            station.setWeekendTimetable(currentStationWeekendTimetable.toString());

            stationService.saveStation(station);
        }

    }
}
