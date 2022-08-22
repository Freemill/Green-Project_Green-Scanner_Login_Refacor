package com.garb.gbcollector.login.web.validation;

import com.garb.gbcollector.login.web.validation.form.MemberSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/validation/api/members")
public class ValidationMemberApiController {

    @PostMapping("/add")
    public Object addMember(@RequestBody @Validated MemberSaveForm form, BindingResult bindingResult) {
        log.info("API 컨틀롤러 호출");

        if (bindingResult.hasErrors()) {
            log.info("검증 오류 발생 errors = {}", bindingResult);
            return bindingResult.getAllErrors();
        }

        log.info("성공 로직 실행");

        return form;
    }
}
