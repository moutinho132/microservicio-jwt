package com.practica.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

@AllArgsConstructor
@Getter
@Builder
public class User {
    private final Integer id;
    @With
    private final String username;
    @With
    private final String password;
    @With
    private final String email;
    @With
    private final String token;
    @With
    private final Boolean status;
}
