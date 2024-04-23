package com.midsummra.subway.service;

import com.midsummra.subway.common.utils.StationUtils;
import com.midsummra.subway.entity.dto.Timetable;
import com.midsummra.subway.entity.dto.Timetables;
import com.midsummra.subway.entity.mysql.StationDistance;
import com.midsummra.subway.entity.mysql.Subway;
import com.midsummra.subway.entity.neo4j.Station;
import com.midsummra.subway.repository.SQLStationRepo;
import com.midsummra.subway.repository.StationDistanceRepo;
import com.midsummra.subway.repository.StationRepo;
import com.midsummra.subway.repository.SubwayRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;

@Service
public class LineServiceImpl implements LineService{

    private final StationRepo stationRepo;

    private final SQLStationRepo sqlStationRepo;

    private final StationDistanceRepo stationDistanceRepo;

    private final SubwayRepo subwayRepo;

    private final StationService stationService;

    private final StationUtils stationUtils;

    @Autowired
    public LineServiceImpl(StationRepo stationRepo, SQLStationRepo sqlStationRepo, StationDistanceRepo stationDistanceRepo,
                           SubwayRepo subwayRepo, StationService stationService, StationUtils stationUtils) {
        this.stationRepo = stationRepo;
        this.sqlStationRepo = sqlStationRepo;
        this.stationDistanceRepo = stationDistanceRepo;
        this.subwayRepo = subwayRepo;
        this.stationService = stationService;
        this.stationUtils = stationUtils;
    }

    @Override
    public ArrayList<Station> deleteLine(String lineName) {
        return stationRepo.deleteLine(lineName);
    }

    @Override
    public ArrayList<Station> recoverLine(String lineName) {
        return stationRepo.recoverLine(lineName);
    }

    @Override
    public Subway findSubway(String lineName) {
        return subwayRepo.findAllByName(lineName);
    }

    @Override
    public ArrayList<Subway> findAllSubway() {
        return (ArrayList<Subway>) subwayRepo.findAll();
    }

    @Override
    @Transactional
    public ArrayList<Station> addLine(String lineName, double speed, ArrayList<String> timetable,
                                      ArrayList<String> stationNames, ArrayList<Double> distances)
                                        throws Exception{

        ArrayList<Station> result = new ArrayList<>();
        Subway subway = addSubway(lineName, speed);
        if (subway == null){
            return new ArrayList<>();
        }
        if (stationNames.size() != distances.size() + 1){
            return new ArrayList<>();
        }
        ArrayList<StationDistance> stationDistancesToSave = new ArrayList<>();
        for (int i = 1; i < stationNames.size(); i++) {
            StationDistance stationDistance = new StationDistance();
            stationDistance.setDistance(distances.get(i - 1));
            stationDistance.setLine(lineName);
            stationDistance.setBlocked(false);
            stationDistance.setTime(distances.get(i - 1) / (speed * 10 / 2 / 36));
            stationDistance.setStationA(stationNames.get(i - 1));
            stationDistance.setStationB(stationNames.get(i));
            stationDistancesToSave.add(stationDistance);
        }
        stationDistanceRepo.saveAll(stationDistancesToSave);
        ArrayList<com.midsummra.subway.entity.mysql.Station> stationsToSave = new ArrayList<>();

        for (int i = 0; i < stationNames.size(); i++) {
            com.midsummra.subway.entity.mysql.Station station = new com.midsummra.subway.entity.mysql.Station();
            station.setLine(lineName);
            station.setStationName(stationNames.get(i));
            if (i != 0){
                station.setPrevStationName(stationNames.get(i - 1));
            }
            if (i != stationNames.size() - 1){
                station.setNextStationName(stationNames.get(i + 1));
            }
            stationsToSave.add(station);
        }
        sqlStationRepo.saveAll(stationsToSave);
        LinkedList<String> strings = new LinkedList<>(timetable);
        readTimetable(strings, lineName);
        readTimetable(strings, lineName);

        stationNames.forEach(stationName -> result.add(stationRepo.findAllByStationName(stationName)));
        return result;
    }

    @Override
    public Subway addSubway(String lineName, double speed) {
        Subway subway = new Subway();
        if (subwayRepo.findAllByName(lineName) == null){
            subway.setBlocked(false);
            subway.setSpeed(speed);
            subway.setName(lineName);
            subway = subwayRepo.save(subway);
        }
        return subway;
    }

    private void readTimetable(LinkedList<String> rawTimetable, String lineName) throws Exception{
        String stationInfo = rawTimetable.pollFirst();
        while (stationInfo.isEmpty()){
            stationInfo = rawTimetable.pollFirst();
        }
        String[] stationInfoArray = stationInfo.split("->");
        com.midsummra.subway.entity.mysql.Station station = stationService.findStation(stationInfoArray[0], lineName);
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
            timetablesWeekday = stationUtils.deserializationTimetables(weekdayTimetable);
        }
        rawTimetable.pollFirst();

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
        while (!(times = rawTimetable.pollFirst()).isEmpty()) {
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
        rawTimetable.pollFirst();

        Timetables timetablesWeekend = null;
        String weekendTimetable = station.getWeekendTimetable();
        if (weekendTimetable == null || weekendTimetable.isEmpty()) {
            timetablesWeekend = new Timetables();
            timetablesWeekend.setDay("weekend");
        } else {
            timetablesWeekend = stationUtils.deserializationTimetables(weekendTimetable);
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

        while (!(times = rawTimetable.pollFirst()).isEmpty()) {
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

            com.midsummra.subway.entity.mysql.Station prevStation = stationService.findStation(prevStationName, lineName);
            String prevStationWeekdayTimetableStr = prevStation.getWeekdayTimetable();
            String prevStationWeekendTimetableStr = prevStation.getWeekendTimetable();

            String currentStationWeekdayTimetableStr = station.getWeekdayTimetable();
            String currentStationWeekendTimetableStr = station.getWeekendTimetable();

            Timetables prevStationWeekdayTimetable = stationUtils.deserializationTimetables(prevStationWeekdayTimetableStr);
            Timetables prevStationWeekendTimetable = stationUtils.deserializationTimetables(prevStationWeekendTimetableStr);

            Timetables currentStationWeekdayTimetable = new Timetables();
            currentStationWeekdayTimetable.setDay("weekday");
            if (currentStationWeekdayTimetableStr != null && !currentStationWeekdayTimetableStr.isEmpty()) {
                currentStationWeekdayTimetable = stationUtils.deserializationTimetables(currentStationWeekdayTimetableStr);
            }
            Timetables currentStationWeekendTimetable = new Timetables();
            currentStationWeekendTimetable.setDay("weekend");
            if (currentStationWeekendTimetableStr != null && !currentStationWeekendTimetableStr.isEmpty()) {
                currentStationWeekendTimetable = stationUtils.deserializationTimetables(currentStationWeekendTimetableStr);
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
