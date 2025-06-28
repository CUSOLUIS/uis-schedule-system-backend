package com.uis.schedule.backend.presentation.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@PreAuthorize("denyAll()")
public class Prueba {

    @GetMapping("/hola")
    @PreAuthorize("permitAll()")
    public String prueba(){
        return "Hola mundo";
    }


    @GetMapping("/hola-secured")
    @PreAuthorize("hasAuthority('READ')")
    public String pruebaSecured(){
        return "Hola mundo secured";
    }

    @GetMapping("/hola-secured2")

    public String pruebaSecured2(){
        return "Hola mundo secured2";
    }
}
