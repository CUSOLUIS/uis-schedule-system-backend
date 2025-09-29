package com.uis.schedule.backend.presentation.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prueba")
@PreAuthorize("denyAll()")
public class Prueba {

    @GetMapping("/hola")
    @PreAuthorize("hasAuthority('VIEW_SCHEDULE') or hasAuthority('APPROVE_PUBLISH_SCHEDULE')")
    public String prueba(){
        return "Hola mundo";
    }


    @GetMapping("/hola-secured")
    @PreAuthorize("hasAuthority('APPROVE_PUBLISH_SCHEDULE')")
    public String pruebaSecured(){
        return "Hola mundo secured";
    }

    @GetMapping("/hola-secured2")

    public String pruebaSecured2(){
        return "Hola mundo secured2";
    }
}
