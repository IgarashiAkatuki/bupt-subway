package com.midsummra.subway.entity.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Timetable {

    private String from;

    private String to;

//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") //此注解用来接收字符串类型的参数封装成LocalDateTime类型
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8", shape = JsonFormat.Shape.STRING) //此注解将date类型数据转成字符串响应出去
//    @JsonDeserialize(using = LocalTimeDeserializer.class)		// 反序列化
//    @JsonSerialize(using = LocalTimeSerializer.class)		// 序列化
    private ArrayList<LocalTime> timetable;


    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.writeValueAsString(this);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
