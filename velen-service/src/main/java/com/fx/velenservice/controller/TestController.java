package com.fx.velenservice.controller;

import com.fx.velenservice.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/home")
    public String test(){
        return testService.test();
    }
}
