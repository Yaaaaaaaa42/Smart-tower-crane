package com.yang.springbootbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @className: DeleteRequest
 * @description: 删除请求类
 * @author YangMaozhi
 * @date 2025/5/7 15:31
 * @version 1.0
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
