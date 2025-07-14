package com.yang.springbootbackend.common;

import com.yang.springbootbackend.constant.CommonConstant;
import com.yang.springbootbackend.exception.ErrorCode;

/**
 * 响应构建器
 * 提供更灵活的响应构建方法
 */
public class ResponseBuilder {

    /**
     * 构建成功响应
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 响应对象
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(CommonConstant.SUCCESS_CODE, data, "操作成功");
    }

    /**
     * 构建成功响应（带自定义消息）
     *
     * @param data    数据
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<>(CommonConstant.SUCCESS_CODE, data, message);
    }

    /**
     * 构建成功响应（无数据）
     *
     * @param message 消息
     * @return 响应对象
     */
    public static BaseResponse<Void> success(String message) {
        return new BaseResponse<>(CommonConstant.SUCCESS_CODE, null, message);
    }

    /**
     * 构建失败响应
     *
     * @param errorCode 错误码
     * @return 响应对象
     */
    public static BaseResponse<Void> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage());
    }

    /**
     * 构建失败响应（带自定义消息）
     *
     * @param errorCode 错误码
     * @param message   自定义消息
     * @return 响应对象
     */
    public static BaseResponse<Void> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }

    /**
     * 构建失败响应（自定义错误码和消息）
     *
     * @param code    错误码
     * @param message 错误消息
     * @return 响应对象
     */
    public static BaseResponse<Void> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * 构建参数错误响应
     *
     * @param message 错误消息
     * @return 响应对象
     */
    public static BaseResponse<Void> paramError(String message) {
        return error(ErrorCode.PARAMS_ERROR, message);
    }

    /**
     * 构建未登录响应
     *
     * @return 响应对象
     */
    public static BaseResponse<Void> notLogin() {
        return error(ErrorCode.NOT_LOGIN_ERROR);
    }

    /**
     * 构建无权限响应
     *
     * @return 响应对象
     */
    public static BaseResponse<Void> noAuth() {
        return error(ErrorCode.NO_AUTH_ERROR);
    }

    /**
     * 构建系统错误响应
     *
     * @return 响应对象
     */
    public static BaseResponse<Void> systemError() {
        return error(ErrorCode.SYSTEM_ERROR);
    }

    /**
     * 构建系统错误响应（带自定义消息）
     *
     * @param message 错误消息
     * @return 响应对象
     */
    public static BaseResponse<Void> systemError(String message) {
        return error(ErrorCode.SYSTEM_ERROR, message);
    }

    /**
     * 构建操作失败响应
     *
     * @param message 错误消息
     * @return 响应对象
     */
    public static BaseResponse<Void> operationError(String message) {
        return error(ErrorCode.OPERATION_ERROR, message);
    }

    /**
     * 构建分页响应
     *
     * @param data       数据列表
     * @param total      总数
     * @param pageNum    页码
     * @param pageSize   页面大小
     * @param <T>        数据类型
     * @return 分页响应对象
     */
    public static <T> BaseResponse<PageResult<T>> page(java.util.List<T> data, long total, 
                                                      int pageNum, int pageSize) {
        PageResult<T> pageResult = new PageResult<>(data, total, pageNum, pageSize);
        return success(pageResult, "查询成功");
    }

    /**
     * 分页结果内部类
     */
    public static class PageResult<T> {
        private java.util.List<T> records;
        private long total;
        private int pageNum;
        private int pageSize;
        private int totalPages;

        public PageResult(java.util.List<T> records, long total, int pageNum, int pageSize) {
            this.records = records;
            this.total = total;
            this.pageNum = pageNum;
            this.pageSize = pageSize;
            this.totalPages = (int) Math.ceil((double) total / pageSize);
        }

        // Getters and Setters
        public java.util.List<T> getRecords() { return records; }
        public void setRecords(java.util.List<T> records) { this.records = records; }
        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
        public int getPageNum() { return pageNum; }
        public void setPageNum(int pageNum) { this.pageNum = pageNum; }
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
}
