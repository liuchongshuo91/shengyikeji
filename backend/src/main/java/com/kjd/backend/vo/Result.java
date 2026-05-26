package com.kjd.backend.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private int code;
    private T data;
    private String msg;

    public Result(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static <T> Result<T> success(T data){
        return new Result<>(200,data,"success");
    }

    public static <T> Result<T> fail(String msg){
        return new Result<>(500,null,msg);
    }

}
