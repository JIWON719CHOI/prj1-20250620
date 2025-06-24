package com.example.prj1.member.controller;

import com.example.prj1.member.dto.MemberDto;
import com.example.prj1.member.dto.MemberForm;
import com.example.prj1.member.service.MemberService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("signup")
    public String signupForm() {
        return "member/signup";
    }

    @PostMapping("signup")
    public String signup(@Valid MemberForm data, BindingResult bindingResult, RedirectAttributes rttr) {
        if (bindingResult.hasErrors()) {
            rttr.addFlashAttribute("errors", bindingResult.getAllErrors());
            rttr.addFlashAttribute("alert", Map.of("code", "danger", "message", "필수 항목을 모두 입력해주세요"));
            return "redirect:/member/signup";
        }

        memberService.add(data);
        rttr.addFlashAttribute("alert", Map.of("code", "success", "message", "회원 가입되었습니다."));
        return "redirect:/board/list";
    }

    @GetMapping("list")
    public String list(Model model) {
        model.addAttribute("memberList", memberService.list());
        return "member/list";
    }

    @GetMapping("detail")
    public String detail(@RequestParam String id, Model model) {
        model.addAttribute("member", memberService.get(id));
        return "member/detail";
    }

    @PostMapping("remove")
    public String remove(MemberForm data,
                         HttpSession session,
                         RedirectAttributes rttr) {
        boolean result = memberService.remove(data);
        if (result) {
            session.invalidate(); // 💡 자동 로그아웃
            rttr.addFlashAttribute("alert", Map.of("code", "info", "message", "회원 탈퇴가 완료되었습니다."));
            return "redirect:/member/login"; // 로그인 페이지로 이동
        } else {
            rttr.addFlashAttribute("alert", Map.of("code", "warning", "message", "비밀번호가 틀렸습니다."));
            rttr.addAttribute("id", data.getId());
            return "redirect:/member/detail";
        }
    }


    @GetMapping("edit")
    public String edit(@RequestParam(required = false) String id, Model model, RedirectAttributes rttr) {
        if (id == null || id.isEmpty()) {
            rttr.addFlashAttribute("alert", Map.of("code", "warning", "message", "잘못된 접근입니다."));
            return "redirect:/member/list";
        }
        model.addAttribute("member", memberService.get(id));
        return "member/edit";
    }

    @PostMapping("edit")
    public String edit(MemberForm data,
                       @SessionAttribute(value = "loggedInUser", required = false) MemberDto user,
                       HttpSession session,
                       RedirectAttributes rttr) {

        boolean result = memberService.update(data, user, session);

        if (result) {
            rttr.addFlashAttribute("alert",
                    Map.of("code", "success", "message", "회원 정보가 변경되었습니다."));
            rttr.addAttribute("id", data.getId());
            return "redirect:/member/detail";
        } else {
            rttr.addAttribute("id", data.getId());
            rttr.addFlashAttribute("alert",
                    Map.of("code", "warning", "message", "암호가 일치하지 않습니다."));
            return "redirect:/member/edit";
        }
    }


    @PostMapping("changePw")
    public String changePassword(@ModelAttribute MemberForm data, RedirectAttributes rttr) {
        boolean ok = memberService.changePassword(data.getId(), data.getOldPassword(), data.getNewPassword());
        if (ok) {
            rttr.addFlashAttribute("alert", Map.of("code", "success", "message", "비밀번호가 정상적으로 변경되었습니다."));
        } else {
            rttr.addFlashAttribute("alert", Map.of("code", "warning", "message", "기존 비밀번호가 일치하지 않습니다."));
        }
        rttr.addAttribute("id", data.getId());
        return "redirect:/member/detail";
    }

    @GetMapping("login")
    public String loginForm() {

        return "member/login";
    }

    @PostMapping("login")
    public String loginProcess(String id, String password,
                               HttpSession session,
                               RedirectAttributes rttr) {

        boolean result = memberService.login(id, password, session);

        if (result) {
            rttr.addFlashAttribute("alert",
                    Map.of("code", "success",
                            "message", "로그인 되었습니다."));
            // 로그인 성공
            return "redirect:/board/list";
        } else {
            rttr.addFlashAttribute("alert",
                    Map.of("code", "warning",
                            "message", "아이디/패스워드가 일치하지 않습니다."));

            // 로그인 실패
            return "redirect:/member/login";
        }

    }

    @RequestMapping("logout")
    public String logout(HttpSession session, RedirectAttributes rttr) {
        session.invalidate();

        rttr.addFlashAttribute("alert",
                Map.of("code", "success",
                        "message", "로그아웃 되었습니다."));

        return "redirect:/board/list";
    }
}
