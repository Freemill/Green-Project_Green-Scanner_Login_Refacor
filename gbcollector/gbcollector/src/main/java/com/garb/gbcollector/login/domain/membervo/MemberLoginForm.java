package com.garb.gbcollector.login.domain.membervo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MemberLoginForm {

    @NotEmpty
    private String userEmail;

    @NotEmpty
    private String password;

}
