package com.garb.gbcollector.domain.validation;

import com.garb.gbcollector.login.domain.membervo.Member;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BeanValidationTest {

    @Test
    void beanValidation(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Member member = new Member();
        member.setNumber(0);
        member.setUserEmail("sfsadf");
        member.setNickName(" ");
        member.setPassword(" ");
        member.setPasswordConfirm("as");
        member.setPrivacyCheck(false);
        member.setTermsCheck(false);

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        for (ConstraintViolation<Member> violation : violations) {
            System.out.println("violation = " + violation);
            System.out.println("violation.getMessage() = " + violation.getMessage());
        }

    }
}
