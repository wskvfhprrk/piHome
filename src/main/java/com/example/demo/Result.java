package com.example.demo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 何建哲
 * @date 2018/12/12 7:43
 */
@Data
public class Result<T> implements Serializable {
    private Boolean success;
    private Integer code;
    private String msg;
    private T content;

    public Result() {
    }

    public Result(Boolean success, int code, String msg) {
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    public Result(Boolean success, int code, String msg, T content) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.content = content;
    }



    public static Result error(int ret, String msg) {
        return new Result(false, ret, msg, "{}");
    }

    public static Result error(int code) {
        return new Result(false, code, "error", "{}");
    }

    public static Result ok() {
        return new Result(true, 200, "ok", "{}");
    }

    public static <T> Result ok(T content) {
        if (null == content) {
            return new Result(true, 200, "ok", content);
        }
        //处理返回结果为集合是空值，统一处理为null
        Class<?> aClass = content.getClass();
        if (aClass == ArrayList.class) {
            List list = (List) content;
            if (list.size() == 0) {
                content = null;
            }
        }
        return new Result(true, 200, "ok", content);
    }


}
