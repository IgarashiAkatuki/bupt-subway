package com.midsummra.subway;

import com.midsummra.subway.common.utils.StationUtils;
import com.midsummra.subway.entity.dto.KShortestPathDTO;
import com.midsummra.subway.entity.mysql.StationDistance;
import com.midsummra.subway.entity.neo4j.Edge;
import com.midsummra.subway.entity.neo4j.Station;
import com.midsummra.subway.repository.EdgeRepo;
import com.midsummra.subway.repository.SQLStationRepo;
import com.midsummra.subway.repository.StationDistanceRepo;
import com.midsummra.subway.repository.StationRepo;
import com.midsummra.subway.service.StationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

@SpringBootTest
class SubwayApplicationTests {

    @Autowired
    private StationDistanceRepo stationDistanceRepo;

    @Autowired
    private EdgeRepo edgeRepo;

    @Autowired
    private StationRepo stationRepo;

    @Autowired
    private SQLStationRepo sqlStationRepo;

    @Autowired
    private StationService stationService;

    @Autowired
    private StationUtils stationUtils;

//    @Test
//    void contextLoads() {
//        String str = "定海园\t定海园西\t1055\n" +
//                "定海园西\t定海园\t880\n" +
//                "定海园西\t经海一路\t1083\n" +
//                "经海一路\t定海园西\t1257\n" +
//                "经海一路\t亦创会展中心\t1368\n" +
//                "亦创会展中心\t经海一路\t1368\n" +
//                "亦创会展中心\t荣昌东街\t608\n" +
//                "荣昌东街\t亦创会展中心\t608\n" +
//                "荣昌东街\t亦庄同仁\t604\n" +
//                "亦庄同仁\t荣昌东街\t604\n" +
//                "亦庄同仁\t鹿圈东\t1228\n" +
//                "鹿圈东\t亦庄同仁\t1228\n" +
//                "鹿圈东\t泰河路\t739\n" +
//                "泰河路\t鹿圈东\t739\n" +
//                "泰河路\t九号村\t606\n" +
//                "九号村\t泰河路\t606\n" +
//                "九号村\t四海庄\t993\n" +
//                "四海庄\t九号村\t993\n" +
//                "四海庄\t太和桥北\t638\n" +
//                "太和桥北\t四海庄\t638\n" +
//                "太和桥北\t瑞合庄\t1208\n" +
//                "瑞合庄\t太和桥北\t1375\n" +
//                "瑞合庄\t融兴街\t798\n" +
//                "融兴街\t瑞合庄\t802\n" +
//                "融兴街\t屈庄\t787\n" +
//                "屈庄\t融兴街\t801";
//        String[] split = str.split("\n");
//        int i = 0;
//        for (String s : split) {
//            i++;
//            if (i % 2 == 0){
//                continue;
//            }
//            String[] split1 = s.split("\t");
//            if (split1.length != 3){
//                continue;
//            }
////            for (String string : split1) {
////                System.out.println(string);
////            }
//            StationDistance stationDistance = new StationDistance();
//            stationDistance.setStationA(split1[0]);
//            stationDistance.setStationB(split1[1]);
//            stationDistance.setDistance(Double.parseDouble(split1[2]));
//            stationDistance.setLine("t1");
//            stationDistanceRepo.save(stationDistance);
//        }
//    }
//
//    @Test
//    void generateNode(){
//        Iterable<StationDistance> stations = stationDistanceRepo.findAll();
//        stations.forEach(station -> {
//            String stationA = station.getStationA();
//            Station a = stationRepo.findAllByStationName(stationA);
//            if (a == null){
//                a = new Station();
//                a.setStationName(stationA);
//                a = stationRepo.save(a);
//            }
//
//            String stationB = station.getStationB();
//            Station b = stationRepo.findAllByStationName(stationB);
//            if (b == null){
//                b = new Station();
//                b.setStationName(stationB);
//                b = stationRepo.save(b);
//            }
//            Edge edge = new Edge();
//            edge.setDistance(station.getDistance());
//            edge.setLine(station.getLine());
//            edge.setTargetStation(b);
//            a.addEdge(edge);
//            stationRepo.save(a);
//        });
//    }
//
//    @Test
//    void test2(){
//        Station station = new Station();
////        station.setStationName("A");
////        station = stationRepo.save(station);
////        Station target = new Station();
////        target.setStationName("B");
////        target = stationRepo.save(target);
////        Edge edge = new Edge();
////        edge.setTargetStation(target);
////        edge.setDistance(100.0);
//////        edge = edgeRepo.save(edge);
////        ArrayList<Edge> list = new ArrayList<>();
////        list.add(edge);
////        station.addEdge(list);
////        stationRepo.save(station);
//    }
//
//    @Test
//    @Transactional
//    void Test3(){
//        System.out.println(System.getProperty("user.dir"));
//
//    }
//
////    @Test
////    void Test4() throws Exception{
////        String property = System.getProperty("user.dir");
////        BufferedReader fileReader = new BufferedReader(new FileReader(property + "/src/main/resources/timetable/line1.txt"));
////        String s = fileReader.readLine();
////        String[] split = s.split("->");
////        StationDistance stationDistance = stationService.findStationDistanceByFrom(split[0], "1");
////
////        ArrayList<LocalTime> listA = new ArrayList<>();
////        Timetable timetableA = new Timetable(stationDistance.getStationA(), stationDistance.getStationB(), listA);
////
////        fileReader.readLine();
////        String temp = null;
////        while (!(temp = fileReader.readLine()).isEmpty()){
////            System.out.println(temp);
////            String[] strings = temp.split(" ");
////            int hour = Integer.parseInt(strings[0]);
////            for (int i = 1; i < strings.length; i++) {
////                int minute = Integer.parseInt(strings[i]);
////                listA.add(LocalTime.of(hour, minute));
////            }
////        }
////
////        String s1 = fileReader.readLine();
////        System.out.println(s1);
////        String[] split1 = s1.split("->");
////        StationDistance stationDistanceByTo = stationService.findStationDistanceByTo(split1[0], "bt");
////        ArrayList<LocalTime> listB = new ArrayList<>();
////        Timetable timetableB = new Timetable(stationDistanceByTo.getStationB(), stationDistanceByTo.getStationA(), listB);
////        fileReader.readLine();
////        temp = null;
////        while (!(temp = fileReader.readLine()).isEmpty()){
////            String[] strings = temp.split(" ");
////            int hour = Integer.parseInt(strings[0]);
////            for (int i = 1; i < strings.length; i++) {
////                int minute = Integer.parseInt(strings[i]);
////                listB.add(LocalTime.of(hour, minute));
////            }
////        }
////
////        Timetables timetables = new Timetables();
////        timetables.setDay("weekday");
////        timetables.setTimetableA(timetableA);
////        timetables.setTimetableB(timetableB);
////
////        com.midsummra.subway.entity.mysql.Station station = new com.midsummra.subway.entity.mysql.Station();
////        station.setLine("1");
////        station.setStationName(split[0]);
////        station.setWeekdayTimetable(timetables.toString());
////
////        System.out.println(stationService.saveStation(station));
////    }
//
//    @Test
//    void Test5(){
//        Iterable<StationDistance> all = stationDistanceRepo.findAll();
//        all.forEach(stationDistance -> {
//            String stationA = stationDistance.getStationA();
//            String stationB = stationDistance.getStationB();
//            String line = stationDistance.getLine();
//            com.midsummra.subway.entity.mysql.Station station = new com.midsummra.subway.entity.mysql.Station();
//            station.setStationName(stationA);
//            station.setLine(line);
//            com.midsummra.subway.entity.mysql.Station station1 = stationService.findStation(stationA, line);
//            if (station1 == null){
//                stationService.saveStation(station);
//            }
//            station.setStationName(stationB);
//            station1 = stationService.findStation(stationB, line);
//            if (station1 == null){
//                stationService.saveStation(station);
//            }
//        });
//    }
//
//    @Test
//    void Test6(){
//        Iterable<com.midsummra.subway.entity.mysql.Station> all = sqlStationRepo.findAll();
//        all.forEach(station -> {
//            ArrayList<StationDistance> distanceByTo = stationService.findStationDistanceByTo(station.getStationName(), station.getLine());
//            if (distanceByTo != null){
//                if (distanceByTo.size() == 2){
//                    System.out.println(distanceByTo);
//                    station.setNextStationName(distanceByTo.get(0).getStationA());
//                }else {
//                    if (!distanceByTo.isEmpty()){
//                        station.setNextStationName(distanceByTo.get(0).getStationA());
//                    }
//                }
//            }
//            ArrayList<StationDistance> distanceByFrom = stationService.findStationDistanceByFrom(station.getStationName(), station.getLine());
//            if (distanceByFrom != null){
//                if (distanceByFrom.size() == 2){
//                    System.out.println(distanceByFrom);
//                }else {
//                    if (!distanceByFrom.isEmpty()){
//                        station.setPrevStationName(distanceByFrom.get(0).getStationB());
//                    }
//                }
//            }
//            stationService.saveStation(station);
//        });
//
//    }
//
//    @Test
//    void Test7() throws Exception{
//        stationUtils.readTimetable("line10.txt", "10");
//    }
//
//    @Test
//    void saveNodes(){
//        stationDistanceRepo.findAll().forEach(stationDistance -> {
//            String stationA = stationDistance.getStationA();
//            String stationB = stationDistance.getStationB();
//            Station station = stationRepo.findAllByStationName(stationA);
//            if (station == null){
//                station = new Station();
//                station.setStationName(stationA);
//                station.setBlocked(false);
//                station.addLine(stationDistance.getLine());
//                stationRepo.save(station);
//            }else {
//                if (!station.getLine().contains(stationDistance.getLine())){
//                    station.addLine(stationDistance.getLine());
//                    stationRepo.save(station);
//                }
//            }
//
//
//            station = stationRepo.findAllByStationName(stationB);
//            if (station == null){
//                station = new Station();
//                station.setStationName(stationB);
//                station.setBlocked(false);
//                station.addLine(stationDistance.getLine());
//                stationRepo.save(station);
//            }else {
//                if (!station.getLine().contains(stationDistance.getLine())){
//                    station.addLine(stationDistance.getLine());
//                    stationRepo.save(station);
//                }
//            }
//
//        });
//    }
//
//
//    @Test
//    void saveEdges() throws Exception{
//        stationDistanceRepo.findAll().forEach(stationDistance -> {
//            String stationA = stationDistance.getStationA();
//            String stationB = stationDistance.getStationB();
//            Edge edge = edgeRepo.findAllByLineAndStationAAndStationB(stationDistance.getLine(),
//                    stationA, stationB);
//            if (edge != null){
//                System.out.println(edge);
//                return;
//            }
//            edge = new Edge();
//            edge.setDistance(stationDistance.getDistance());
//            edge.setLine(stationDistance.getLine());
//            edge.setTime(stationDistance.getTime());
//            edge.setStationA(stationA);
//            edge.setStationB(stationB);
//            Station station1 = stationRepo.findAllByStationName(stationA);
//            Station station2 = stationRepo.findAllByStationName(stationB);
//            edge.setTargetStation(station2);
//            station1.addEdge(edge);
//            stationRepo.save(station1);
//        });
////        edgeRepo.saveAll(edgeToSave);
//    }
//
//
//    @Test
//    void Test8(){
//        Edge edge = edgeRepo.findAllByLineAndStationAAndStationB("10",
//                "西二旗", "生命科学园");
//        System.out.println(edge);
//        StationDistance stationDistanceByStationAAndStationBAndLine = stationDistanceRepo.findStationDistanceByStationAAndStationBAndLine("西二旗", "生命科学园", "cp");
//        StationDistance stationDistanceByStationAAndStationBAndLine1 = stationDistanceRepo.findStationDistanceByStationAAndStationBAndLine("生命科学园","西二旗",  "cp");
//        System.out.println(stationDistanceByStationAAndStationBAndLine);
//        System.out.println(stationDistanceByStationAAndStationBAndLine1);
//    }
//
//    @Test
//    void Test9() throws Exception{
//        ArrayList<KShortestPathDTO> kShortestPath = stationService.findKShortestPath("朱辛庄", "环球度假区", 10);
//        stationService.calculatePath(kShortestPath, LocalTime.of(18,10), false).forEach(stationPath -> {
//            System.out.println(stationPath.getExchanges());
//            System.out.println(stationPath.getPath());
//            System.out.println(stationPath.getExchangeStation());
//            System.out.println(stationPath.getStartTime());
//            System.out.println(stationPath.getEndTime());
//            System.out.println(stationPath.getTotalCost());
//            System.out.println("============================");
//            stationPath.getExchangeStation();
//        });
//    }
//
//    @Test
//    void Test10() throws Exception{
//        System.out.println(stationRepo.graphExist("test"));
//        try {
//            stationRepo.generateProjectGraph();
//        }catch (Exception e){
//            System.out.println("graph初始化完成");
//        }
//        System.out.println(stationRepo.graphExist("test"));
//    }
}
