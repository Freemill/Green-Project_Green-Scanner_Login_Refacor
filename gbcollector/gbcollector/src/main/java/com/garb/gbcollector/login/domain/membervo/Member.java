package com.garb.gbcollector.login.domain.membervo;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Member {

    private Long id;

    @NotNull
    @Range(min=1, max=1000)
    private Integer number;

    @NotBlank
    @Email
    private String userEmail;

    @NotBlank
    private String nickName;

    @NotBlank
    private String password;

    @NotBlank
    private String passwordConfirm;

    @AssertTrue
    private boolean privacyCheck;

    @AssertTrue
    private boolean termsCheck;

    public Member(){}

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
