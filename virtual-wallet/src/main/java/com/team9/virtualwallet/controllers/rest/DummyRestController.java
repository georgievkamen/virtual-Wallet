package com.team9.virtualwallet.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api/dummy")
public class DummyRestController {

    @Autowired
    public DummyRestController() {
    }

    @GetMapping
    public boolean rejected() {
        Random rd = new Random();
        return rd.nextBoolean();
    }

}
