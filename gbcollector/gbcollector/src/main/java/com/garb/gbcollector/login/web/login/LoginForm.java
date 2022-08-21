package com.garb.gbcollector.login.web.login;

import javax.validation.constraints.NotEmpty;

public class LoginForm {

    @NotEmpty
    private String userEmail;

    @NotEmpty
    private String password;
}
