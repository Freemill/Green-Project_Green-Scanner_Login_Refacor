package com.garb.gbcollector.login.domain.membervo;

import lombok.Data;

@Data
public class Member {

    private Long id;

    private String name;

    private String userEmail;

    private String nickName;

    private String password;

    private String passwordConfirm;

    private boolean privacyCheck;

    private boolean termsCheck;
}
