package com.yang.springbootbackend.exception;

import lombok.Getter;

/**
 * @className: BusinessException
 * @description: 基本业务异常
 * @author YangMaozhi
 * @date 2025/5/7 15:22
 * @version 1.0
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

}
