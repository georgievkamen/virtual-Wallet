package com.team9.virtualwallet.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/dummy")
public class DummyRestController {

    @Autowired
    public DummyRestController() {
    }

    @GetMapping
    public HttpStatus status() {
        List<HttpStatus> statusList = new ArrayList<>();
        statusList.add(HttpStatus.OK);
        statusList.add(HttpStatus.I_AM_A_TEAPOT);

        Random random = new Random();
        int randomInt = random.nextInt(2);

        return statusList.get(randomInt);
    }

}
