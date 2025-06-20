package com.example.prj1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("main1")
public class TestController {

    @GetMapping("/sub1")
    public String sub1() {

        return "main1/sub1";
    }
}
