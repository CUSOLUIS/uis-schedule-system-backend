package com.uis.schedule.backend.presentation.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class Prueba {

    @GetMapping("/hola")
    public String prueba(){
        return "Hola mundo";
    }
    @GetMapping("/hola-secured")
    public String pruebaSecured(){
        return "Hola mundo secured";
    }
}
