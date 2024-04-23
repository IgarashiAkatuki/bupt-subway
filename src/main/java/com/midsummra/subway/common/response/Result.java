package com.midsummra.subway.common.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Result<T> {

    // 状态码
    private int statusCode;

    // 提示信息
    private String message;

    // 返回数据
    private T data;

    public Result(){};

    public Result(int statusCode, String message){
        this.statusCode = statusCode;
        this.message = message;
    }

    public static <T> Result<T> succeed(){
        return succeed(null, StatusCode.SUCCESS.getResultMessage());
    }

    public static <T> Result<T> succeed(T data){
        return succeed(data, StatusCode.SUCCESS.getResultMessage());
    }

    /**
     *
     * @param data 要返回的值
     * @param msg 提示信息
     * @return 封装后的Result对象
     * @param <T> 要返回值的类型
     * @see Result
     * @see StatusCode
     */
    public static <T> Result<T> succeed(T data, String msg){
        Result<T> result = new Result<>();
        result.setData(data);
        result.setMessage(msg);
        result.setStatusCode(StatusCode.SUCCESS.getStatusCode());

        return result;
    }

    public static <T> Result<T> error(){
        return error(StatusCode.FAILED.getStatusCode(), StatusCode.FAILED.getResultMessage());
    }

    /**
     *
     * @param statusCode 状态码
     * @param msg 错误信息
     * @return 封装后的Result
     * @param <T>
     * @see Result
     * @see StatusCode
     */
    public static <T> Result<T> error(int statusCode, String msg){
        Result<T> result = new Result<>();
        result.setMessage(msg);
        result.setStatusCode(statusCode);

        return result;
    }


    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 重写toString
     * @return 序列化的Result
     * @see ObjectMapper
     * @see Result
     */
    @Override
    public String toString(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        }catch (JsonProcessingException e){
            throw new RuntimeException();
        }
    }
}
