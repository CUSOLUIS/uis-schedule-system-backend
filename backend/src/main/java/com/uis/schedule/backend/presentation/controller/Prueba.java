package com.uis.schedule.backend.presentation.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Prueba {

    @GetMapping("/hola")
    public String prueba(){
        return "Hola mundo";
    }
}
