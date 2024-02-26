package com.practica.security.controller;

import com.practica.security.controller.request.UserLoginRequest;
import com.practica.security.controller.request.UserRequest;
import com.practica.security.model.User;
import com.practica.security.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/security")
public class UserController implements UserApi {
    private final UserService service;
    @Override
    public User login(UserLoginRequest request) {
        return service.validateUser(request);
    }

    @Override
    public User saveUser(UserRequest request) throws Exception {
        return service.save(request);
    }

    @Override
    public User findById(Integer id,final String token) throws Exception {
            service.findCurrentUser(token);
        return service.findByUser(id).withToken(token);
    }

    @Override
    public User findCurrentUser(String token) throws Exception {
        return service.findCurrentUser(token);
    }

    @Override
    public String test() throws Exception {
        return "hello mundo";
    }
}
