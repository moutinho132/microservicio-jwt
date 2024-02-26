package com.practica.security.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserRequest {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private Boolean status;
}
