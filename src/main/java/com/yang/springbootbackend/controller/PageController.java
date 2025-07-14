package com.yang.springbootbackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PageController {

    /**
     * 传感器监控页面
     */
    @GetMapping("/monitor")
    public String sensorMonitor() {
        return "sensor-monitor.html";
    }

    /**
     * 验证码测试页面
     */
    @GetMapping("/image-code")
    public String imageCode() {
        return "image-code.html";
    }

}