package com.midsummra.subway.entity.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Timetables {

    private String day;

    private Timetable timetableA;

    private Timetable timetableB;

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String val = null;
        try {
            val = mapper.writeValueAsString(this);
        }catch (Exception e){
            e.printStackTrace();
        }
        return val;
    }
}
