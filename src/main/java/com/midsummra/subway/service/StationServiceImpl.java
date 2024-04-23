package com.midsummra.subway.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.midsummra.subway.entity.dto.KShortestPathDTO;
import com.midsummra.subway.entity.dto.StationPath;
import com.midsummra.subway.entity.dto.Timetable;
import com.midsummra.subway.entity.dto.Timetables;
import com.midsummra.subway.entity.mysql.Station;
import com.midsummra.subway.entity.mysql.StationDistance;
import com.midsummra.subway.entity.neo4j.Edge;
import com.midsummra.subway.repository.EdgeRepo;
import com.midsummra.subway.repository.SQLStationRepo;
import com.midsummra.subway.repository.StationDistanceRepo;
import com.midsummra.subway.repository.StationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class StationServiceImpl implements StationService{

    private final StationDistanceRepo stationDistanceRepo;

    private final SQLStationRepo sqlStationRepo;

    private final StationRepo stationRepo;

    private final EdgeRepo edgeRepo;

    @Autowired
    public StationServiceImpl(StationDistanceRepo stationDistanceRepo, SQLStationRepo sqlStationRepo, StationRepo stationRepo, EdgeRepo edgeRepo) {
        this.stationDistanceRepo = stationDistanceRepo;
        this.sqlStationRepo = sqlStationRepo;
        this.stationRepo = stationRepo;
        this.edgeRepo = edgeRepo;
    }

    @Override
    public ArrayList<StationDistance> findStationDistanceByFrom(String stationName, String lineName) {
        return stationDistanceRepo.findStationDistanceByStationAAndLine(stationName, lineName);
    }

    @Override
    public ArrayList<StationDistance> findStationDistanceByTo(String stationName, String lineName) {
        return stationDistanceRepo.findStationDistanceByStationBAndLine(stationName, lineName);
    }

    @Override
    public ArrayList<StationDistance> findStationDistance(String stationA, String stationB) {
        ArrayList<StationDistance> stationDistances = stationDistanceRepo.findAllByStationAAndStationB(stationA, stationB);
        if (stationDistances != null && !stationDistances.isEmpty()){
            return stationDistances;
        }
        return stationDistanceRepo.findAllByStationAAndStationB(stationB, stationA);
    }

    @Override
    public Station saveStation(Station station) {
        return sqlStationRepo.save(station);
    }

    @Override
    public Station findStation(String stationName) {
        ArrayList<Station> allByStationName = sqlStationRepo.findAllByStationName(stationName);
        if (allByStationName == null){
            return null;
        }
        return allByStationName.get(0);
    }

    @Override
    public Station findStation(String stationName, String lineName) {
        return sqlStationRepo.findAllByStationNameAndLine(stationName, lineName);
    }

    @Override
    public StationDistance findStationDistance(String stationAName, String stationBName, String lineName) {
        StationDistance station = stationDistanceRepo.findStationDistanceByStationAAndStationBAndLine(stationAName, stationBName, lineName);
        if (station == null){
            station = stationDistanceRepo.findStationDistanceByStationAAndStationBAndLine(stationBName, stationAName, lineName);
        }
        return station;
    }

    @Override
    public ArrayList<KShortestPathDTO> findKShortestPath(String stationAName, String stationBName, int k) {
        return stationRepo.findKShortestPath(stationAName, stationBName, k);
    }

    @Override
    public ArrayList<KShortestPathDTO> findKShortestPath(String stationAName, String stationBName) {
        return findKShortestPath(stationAName, stationBName, 5);
    }


    @Override
    public ArrayList<StationPath> calculatePath(ArrayList<KShortestPathDTO> kShortestPathDTOS) throws Exception {
        return calculatePath(kShortestPathDTOS, null, false);
    }

    @Override
    public ArrayList<StationPath> calculatePath(ArrayList<KShortestPathDTO> kShortestPathDTOS, LocalTime time, boolean isWeekend) throws Exception {
        ArrayList<StationPath> result = new ArrayList<>();
        kShortestPathDTOS.forEach(kShortestPathDTO -> {
            StationPath stationPath = new StationPath(kShortestPathDTO);
            LinkedHashMap<String, ArrayList<String>> exchangeStations = new LinkedHashMap<>();
            int exchanges = 0;
            ArrayList<com.midsummra.subway.entity.neo4j.Station> path = kShortestPathDTO.getPath();
            if (path.size() > 2){
                String currentLineName = path.get(0).getLine().get(0);
                if (path.get(0).getLine().size() > 1){
                    currentLineName = findLineName(path.get(0).getStationName(), path.get(1).getStationName());
                }
                for (int i = 1; i < path.size() - 1; i++) {
                    if (path.get(i).getLine().size() < 2){
                        continue;
                    }

                    String lineName = findLineName(path.get(i).getStationName(), path.get(i + 1).getStationName());
                    if (path.get(i).getLine().equals(path.get(i + 1).getLine()) && i < path.size() - 2){
                        lineName = path.get(i + 2).getLine().get(0);
                    }

                    if (!lineName.equals(currentLineName)){
                        ArrayList<String> exchange = new ArrayList<>();
                        exchange.add(currentLineName);
                        exchange.add(lineName);
                        exchange.add(i + "");
                        currentLineName = lineName;
                        exchangeStations.put(path.get(i).getStationName(), exchange);
                        exchanges++;
                    }
                }
            }
            stationPath.setExchanges(exchanges);
            stationPath.setExchangeStation(exchangeStations);

            LocalTime currentTime = LocalTime.now();
            if (time != null){
                currentTime = time;
            }
            LocalTime currentTimeCopy = currentTime;
            LocalTime endTime = null;
            LocalDate currentDate = LocalDate.now();

            DayOfWeek week = currentDate.getDayOfWeek();
            LinkedHashMap<String, LocalTime> exchangeTimes = new LinkedHashMap<>();
//            stationPath.setStartTime(currentTime);

            LinkedHashMap<String, ArrayList<String>> exchangeStationsCopy = new LinkedHashMap<>(exchangeStations);
            stationPath.setTotalCost(stationPath.getTotalCost() + exchangeStationsCopy.size() * 600);

            String lineName = findLineName(path.get(0).getStationName(), path.get(1).getStationName());
            if (path.size() > 2 && path.get(0).getStationName().equals(path.get(1).getStationName())){
                lineName = path.get(2).getLine().get(0);
            }
            Station station = sqlStationRepo.findAllByStationNameAndLine(path.get(0).getStationName(), lineName);
            Pair<Timetable, Integer> timetableIntegerPair = null;
            try {
                timetableIntegerPair = getTimetable(station, path.get(1).getStationName(), isWeekend);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            int index = getSubwayTimeIndex(currentTime, timetableIntegerPair.getVar1().getTimetable());
            if (index == -1){
                return;
            }
            LocalTime tempTime = timetableIntegerPair.getVar1().getTimetable().get(index);
            stationPath.setStartTime(tempTime);
            long seconds = Duration.between(currentTimeCopy, tempTime).toSeconds();
            System.out.println(seconds);
            stationPath.setTotalCost(stationPath.getTotalCost() + seconds);
            if (exchangeStationsCopy.isEmpty()){

                if (index != -1){
                    stationPath.setStartTime(timetableIntegerPair.getVar1().getTimetable().get(index));
                    Station endStation = sqlStationRepo.findAllByStationNameAndLine(path.get(path.size() - 1).getStationName(), lineName);
                    Timetable endStationTimetable = null;
                    if (isWeekend){
                        try {
                            Timetables deserializationTimetables = deserializationTimetables(endStation.getWeekendTimetable());
                            if (timetableIntegerPair.getVar2() == 1){
                                endStationTimetable = deserializationTimetables.getTimetableA();
                            }else {
                                endStationTimetable = deserializationTimetables.getTimetableB();
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }else {
                        try {
                            Timetables deserializationTimetables = deserializationTimetables(endStation.getWeekdayTimetable());
                            if (timetableIntegerPair.getVar2() == 1){
                                endStationTimetable = deserializationTimetables.getTimetableA();
                            }else {
                                endStationTimetable = deserializationTimetables.getTimetableB();
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    endTime = endStationTimetable.getTimetable().get(index);
                }

                stationPath.setEndTime(tempTime.plusSeconds(stationPath.getTotalCost().longValue()));
            }else {
                if (index != -1){
                    stationPath.setStartTime(timetableIntegerPair.getVar1().getTimetable().get(index));
                    int direction = timetableIntegerPair.getVar2();
                    LinkedList<Map.Entry<String, ArrayList<String>>> entryList = new LinkedList<>(exchangeStationsCopy.entrySet());
                    while (!entryList.isEmpty() && index != -1){
                        Map.Entry<String, ArrayList<String>> entry = entryList.pollFirst();
                        int stationIndex = Integer.parseInt(entry.getValue().get(2));
                        Pair<Timetable, Integer> timetable = null;
                        try {
                            timetable = getTimetable(path.get(stationIndex - 1).getStationName(), path.get(stationIndex).getStationName(),
                                    lineName, isWeekend);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        LocalTime localTime = timetable.getVar1().getTimetable().get(index);
                        exchangeTimes.put(entry.getKey(), localTime);

                        currentTime = localTime.plusSeconds(300);
                        exchangeStations.get(entry.getKey()).add(currentTime.toString());
                        stationPath.setTotalCost(stationPath.getTotalCost() + 300);

                        lineName = entry.getValue().get(1);
                        station = sqlStationRepo.findAllByStationNameAndLine(entry.getKey(), lineName);
                        try {
                            timetable = getTimetable(station, path.get(stationIndex + 1).getStationName(), isWeekend);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        index = getSubwayTimeIndex(currentTime, timetable.getVar1().getTimetable());
                        LocalTime exchangeTime = timetable.getVar1().getTimetable().get(index);
                        seconds = Duration.between(currentTime, exchangeTime).toSeconds();
                        if (seconds < 0){
                            seconds = 0;
                        }
                        System.out.println(seconds);
                        stationPath.setTotalCost(stationPath.getTotalCost() + seconds);
                        direction = timetable.getVar2();
                    }
                    if (index == -1){
                        stationPath.setEndTime(null);
                        return;
                    }
                    Station endStation = sqlStationRepo.findAllByStationNameAndLine(path.get(path.size() - 1).getStationName(), lineName);
                    Timetable endStationTimetable = null;
                    if (isWeekend){
                        try {
                            Timetables deserializationTimetables = deserializationTimetables(endStation.getWeekendTimetable());
                            if (direction == 1){
                                endStationTimetable = deserializationTimetables.getTimetableA();
                            }else {
                                endStationTimetable = deserializationTimetables.getTimetableB();
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }else {
                        try {
                            Timetables deserializationTimetables = deserializationTimetables(endStation.getWeekdayTimetable());
                            if (direction == 1){
                                endStationTimetable = deserializationTimetables.getTimetableA();
                            }else {
                                endStationTimetable = deserializationTimetables.getTimetableB();
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    endTime = endStationTimetable.getTimetable().get(index);
                    stationPath.setEndTime(currentTimeCopy.plusSeconds(stationPath.getTotalCost().longValue()));
                    stationPath.setExchangeStation(exchangeStations);
//                    long seconds = Duration.between(currentTimeCopy, endTime).toSeconds();
                    System.out.println(endTime);
//                    System.out.println(seconds);
//                    stationPath.setTotalCost((double) seconds);
                    stationPath.setExchangeTime(exchangeTimes);
                }else {
                    stationPath.setEndTime(null);
                }
            }
            ArrayList<String> nodeNames = stationPath.getNodeNames();
            for (int i = 1; i < nodeNames.size(); i++) {
                ArrayList<StationDistance> stationDistance = this.findStationDistance(nodeNames.get(i - 1), nodeNames.get(i));
                stationPath.setTotalDistance(stationPath.getTotalDistance() + stationDistance.get(0).getDistance());
            }

            result.add(stationPath);

        });
        return result;
    }

    @Override
    public com.midsummra.subway.entity.neo4j.Station blockStation(String stationName) {
        return stationRepo.updateIsBlockedByStationName(stationName, true);
    }

    @Override
    public com.midsummra.subway.entity.neo4j.Station unBlockStation(String stationName) {
        return stationRepo.updateIsBlockedByStationName(stationName, false);
    }

    @Override
    public ArrayList<com.midsummra.subway.entity.neo4j.Station> findAllStations(boolean isBlocked) {
        return stationRepo.findAllByIsBlocked(isBlocked);
    }

    @Override
    public boolean resetGraph() {
        stationRepo.deleteAllRelationShips();
        stationRepo.deleteAll();
        try {
            stationRepo.deleteGraph("test");
        }catch (Exception e){
            System.out.println("graph已移除");
        }
        stationDistanceRepo.findAll().forEach(stationDistance -> {
            String stationA = stationDistance.getStationA();
            String stationB = stationDistance.getStationB();
            com.midsummra.subway.entity.neo4j.Station station = stationRepo.findAllByStationName(stationA);
            if (station == null){
                station = new com.midsummra.subway.entity.neo4j.Station();
                station.setStationName(stationA);
                station.setBlocked(false);
                station.addLine(stationDistance.getLine());
                stationRepo.save(station);
            }else {
                if (!station.getLine().contains(stationDistance.getLine())){
                    station.addLine(stationDistance.getLine());
                    stationRepo.save(station);
                }
            }
            station = stationRepo.findAllByStationName(stationB);
            if (station == null){
                station = new com.midsummra.subway.entity.neo4j.Station();
                station.setStationName(stationB);
                station.setBlocked(false);
                station.addLine(stationDistance.getLine());
                stationRepo.save(station);
            }else {
                if (!station.getLine().contains(stationDistance.getLine())){
                    station.addLine(stationDistance.getLine());
                    stationRepo.save(station);
                }
            }
        });
        stationDistanceRepo.findAll().forEach(stationDistance -> {
            String stationA = stationDistance.getStationA();
            String stationB = stationDistance.getStationB();
            Edge edge = edgeRepo.findAllByLineAndStationAAndStationB(stationDistance.getLine(),
                    stationA, stationB);
            if (edge != null){
                System.out.println(edge);
                return;
            }
            edge = new Edge();
            edge.setDistance(stationDistance.getDistance());
            edge.setLine(stationDistance.getLine());
            edge.setTime(stationDistance.getTime());
            edge.setStationA(stationA);
            edge.setStationB(stationB);
            com.midsummra.subway.entity.neo4j.Station station1 = stationRepo.findAllByStationName(stationA);
            com.midsummra.subway.entity.neo4j.Station station2 = stationRepo.findAllByStationName(stationB);
            edge.setTargetStation(station2);
            station1.addEdge(edge);
            stationRepo.save(station1);
        });
        try {
            stationRepo.generateProjectGraph();
        }catch (Exception e){
            System.out.println("graph初始化完成");
        }
        return true;
    }


    private int getSubwayTimeIndex(LocalTime currentTime, ArrayList<LocalTime> timetable){
        LocalTime tempTime = null;
        int index = 0;
        for (int i = 0; i < timetable.size(); i++) {
            if (currentTime.isAfter(timetable.get(i)) && Duration.between(timetable.get(i), currentTime).toSeconds() < 60){
                index = i;
                return index;
            }
            if (currentTime.isBefore(timetable.get(i))){
                tempTime = timetable.get(i);
                index = i;
                break;
            }
        }

        if (tempTime != null){
            Duration between = Duration.between(tempTime, currentTime);
            long seconds = between.toSeconds();
            if (seconds >= 900.0){
                return -1;
            }
            return index;
        }

        return -1;
    }

    private String findLineName(String exchangeStationName, String targetStationName){
        ArrayList<Station> stations = sqlStationRepo.findAllByStationName(exchangeStationName);
        for (Station station : stations) {
            if (targetStationName.equals(station.getPrevStationName()) || targetStationName.equals(station.getNextStationName())){
                return station.getLine();
            }
        }
        return null;
    }

    private Timetables deserializationTimetables(String timetables) throws Exception{
        if (timetables == null || timetables.equals("")) {
            return new Timetables();
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(timetables, new TypeReference<Timetables>() {
        });
    }

    private Pair<Timetable, Integer> getTimetable(Station station, String nextStation, boolean isWeekend) throws Exception{
        String timetables = null;
        if (isWeekend){
            timetables = station.getWeekendTimetable();
        }else {
            timetables = station.getWeekdayTimetable();
        }
        Timetables deserializationTimetables = deserializationTimetables(timetables);
        Timetable timetableA = deserializationTimetables.getTimetableA();
        Timetable timetableB = deserializationTimetables.getTimetableB();
        if (nextStation.equals(timetableA.getTo())){
            return new Pair<>(timetableA, 1);
        }
        return new Pair<>(timetableB, 2);
    }

    private Pair<Timetable, Integer> getTimetable(String prevStation, String currentStation, String lineName, boolean isWeekend) throws Exception{
        Station station = sqlStationRepo.findAllByStationNameAndLine(prevStation, lineName);
        String timetables = null;
        int direction = 1;
        if (isWeekend){
            timetables = station.getWeekendTimetable();
        }else {
            timetables = station.getWeekdayTimetable();
        }
        Timetables deserializationTimetables = deserializationTimetables(timetables);
        if (deserializationTimetables.getTimetableA().getTo().equals(currentStation)){
            direction = 1;
        }else {
            direction = 0;
        }
        station = sqlStationRepo.findAllByStationNameAndLine(currentStation, lineName);
        if (isWeekend){
            timetables = station.getWeekendTimetable();
        }else {
            timetables = station.getWeekdayTimetable();
        }
        deserializationTimetables = deserializationTimetables(timetables);
        if (direction == 1){
            return new Pair<>(deserializationTimetables.getTimetableA(), 1);
        }else {
            return new Pair<>(deserializationTimetables.getTimetableB(), 2);
        }
    }
}


class Pair<U, V>{

    private U var1;
    private V var2;

    public Pair(U var1, V var2) {
        this.var1 = var1;
        this.var2 = var2;
    }

    public Pair() {
    }

    public U getVar1() {
        return var1;
    }

    public void setVar1(U var1) {
        this.var1 = var1;
    }

    public V getVar2() {
        return var2;
    }

    public void setVar2(V var2) {
        this.var2 = var2;
    }
}
