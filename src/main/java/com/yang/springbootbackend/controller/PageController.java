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
}