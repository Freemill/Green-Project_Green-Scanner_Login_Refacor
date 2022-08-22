package com.garb.gbcollector.login.domain.membervo;

import lombok.Data;

@Data
public class Member {

    private Long id;

    private Integer number;

    private String userEmail;

    private String nickName;

    private String password;

    private String passwordConfirm;

    private boolean privacyCheck;

    private boolean termsCheck;

    public Member(Integer number, String userEmail, String nickName, String password, String passwordConfirm, boolean privacyCheck, boolean termsCheck) {
        this.number = number;
        this.userEmail = userEmail;
        this.nickName = nickName;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.privacyCheck = privacyCheck;
        this.termsCheck = termsCheck;
    }
}
