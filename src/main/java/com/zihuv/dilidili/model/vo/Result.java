package com.zihuv.dilidili.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    public static final Integer SUCCESS = 200;
    public static final Integer FAIL = 404;

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    public static <T> Result<T> success() {
        return Result.success(null);
    }

    public static <T> Result<T> success(T data) {
        return Result.success("操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return Result.generate(SUCCESS, message, data);
    }

    public static <T> Result<T> fail(String message) {
        return Result.fail(message, null);
    }

    public static <T> Result<T> fail(String message, T data) {
        return Result.generate(FAIL, message, data);
    }

    public boolean isSuccess() {
        return Objects.equals(this.code, SUCCESS);
    }

    /**
     * 封装Result对象，并返回该对象
     *
     * @param code    返回码
     * @param message 返回消息
     * @param data    返回数据
     * @return com.zihuv.common.model.vo.Result<T>
     */
    private static <T> Result<T> generate(Integer code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}