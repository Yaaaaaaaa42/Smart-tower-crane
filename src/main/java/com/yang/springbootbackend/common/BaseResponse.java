package com.yang.springbootbackend.common;

import com.yang.springbootbackend.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @className: BaseResponse<T>
 * @description: 全局异常响应类
 * @author YangMaozhi
 * @date 2025/5/7 15:23
 * @version 1.0
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}

