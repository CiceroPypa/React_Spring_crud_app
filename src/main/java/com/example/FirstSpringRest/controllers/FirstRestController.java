package com.example.FirstSpringRest.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController //@Controller + всі методи мають @ResponseBody
@RequestMapping("/api")
public class FirstRestController {

    @ResponseBody //анотація рест(вказує що ми повертаємо дані)
    @GetMapping("/sayHello")
    public  String sayHello(){
        return "Hello world";
    }
}
