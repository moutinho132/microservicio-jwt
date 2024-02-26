package com.practica.security.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserLoginRequest {
    private String username;
    private String password;
}
