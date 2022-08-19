package com.garb.gbcollector.login.web.validation;

import com.garb.gbcollector.login.domain.memberservice.MemberServiceImpl;
import com.garb.gbcollector.login.domain.membervo.MemberSaveForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ValidationMemberControllerV3 {

    private final MemberServiceImpl memberService;

    @GetMapping("/memberInsertForm")
    public String memberInsert(Model model) {
        log.info("memberInsertForm IN");
        model.addAttribute("member", new MemberSaveForm());
        return "html/memberInsertForm";
    }

    @PostMapping("/memberInsert")
    public String memberInsert(@Validated @ModelAttribute MemberSaveForm member, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if (memberService.nickNameDuplicateCheck(member.getNickName())) {
            bindingResult.rejectValue("nickName", "duplicated");
        }

        // password와 passwordConfirm의 값이 다를 경우 검증! -> 특정 field문제가 아닌 복합 rule 검증!
        if (member.getPassword() != null && member.getPasswordConfirm() != null) {
            String memberPassword = member.getPassword();
            String memberPasswordConfirm = member.getPasswordConfirm();
            if (memberPassword != memberPasswordConfirm) {
                bindingResult.reject("passwordSame");
            }
        }


        //검증에 실패하면 다시 입력 폼으로!
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "html/memberInsertForm";
        }

        // 성공 로직
        memberService.join(member);

        return "redirect:/";
    }

}
