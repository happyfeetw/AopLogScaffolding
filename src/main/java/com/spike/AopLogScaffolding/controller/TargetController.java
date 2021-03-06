package com.spike.AopLogScaffolding.controller;

import com.spike.AopLogScaffolding.annotation.LogTrace;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webLog")
public class TargetController {

    @RequestMapping("/greetings")
    @LogTrace(name = "/webLog/greetings", persistence = false)
    public String greetings(String name){
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return name;
    }
}
