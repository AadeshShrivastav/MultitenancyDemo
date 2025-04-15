package com.divergentsl.multitenant.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserDto {
    private String usernameOrEmail;
    private String password;
}
