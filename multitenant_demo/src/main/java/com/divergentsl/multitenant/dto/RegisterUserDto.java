package com.divergentsl.multitenant.dto;

import com.divergentsl.multitenant.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {

    private String name;
    private String email;
    private String password;
    private String status;
    private Role role;

}
