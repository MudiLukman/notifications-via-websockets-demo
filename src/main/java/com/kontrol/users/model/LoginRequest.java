package com.kontrol.users.model;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    public String username;
    @NotBlank
    public String password;
    @NotBlank
    public String source;
}
