package com.yang.springbootbackend.common;

import lombok.Data;

/**
 * @className: PageRequest
 * @description: 分页请求类
 * @author YangMaozhi
 * @date 2025/5/7 15:30
 * @version 1.0
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}
