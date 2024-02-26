package com.practica.security.controller;

import com.practica.security.controller.request.UserLoginRequest;
import com.practica.security.controller.request.UserRequest;
import com.practica.security.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

public interface UserApi {
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    User login(@RequestBody UserLoginRequest request);

    @PostMapping(path = "/users/save", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    User saveUser(@RequestBody UserRequest request) throws Exception;

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    User findById(@PathVariable("id") Integer id,@RequestHeader(value = "Authorization") String token) throws Exception;

    @GetMapping(path = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    User findCurrentUser(@RequestHeader(value = "Authorization") String token) throws Exception;


    @GetMapping(path = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    String test() throws Exception;
}
