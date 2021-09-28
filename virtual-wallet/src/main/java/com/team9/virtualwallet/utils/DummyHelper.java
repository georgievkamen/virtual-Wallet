package com.team9.virtualwallet.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

public class DummyHelper {

    public static void validateDummy() {
        RestTemplate restTemplate = new RestTemplate();
        HttpStatus status = restTemplate.getForObject("http://localhost/api/dummy", HttpStatus.class);
        if (status != null && status.isError()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sorry your transfer is rejected");
        }
    }
}
