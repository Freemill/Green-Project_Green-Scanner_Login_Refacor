package com.garb.gbcollector.login.web.validation.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class MemberLoginForm {

    @NotEmpty
    @Email
    private String userEmail;

    @NotEmpty
    private String password;

    public MemberLoginForm(String userEmail, String password) {
        this.userEmail = userEmail;
        this.password = password;
    }

}
