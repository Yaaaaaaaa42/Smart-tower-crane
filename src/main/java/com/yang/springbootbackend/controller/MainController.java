package com.yang.springbootbackend.controller;

import com.yang.springbootbackend.common.BaseResponse;
import com.yang.springbootbackend.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {

    @GetMapping("/health")
    public BaseResponse<String> hello() {
        return ResultUtils.success("success");
    }
}
