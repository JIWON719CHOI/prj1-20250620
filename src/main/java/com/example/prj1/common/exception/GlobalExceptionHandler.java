package com.example.prj1.common.exception;

import com.example.prj1.member.exception.DuplicateMemberException;
import com.example.prj1.member.exception.MemberNotFoundException;
import com.example.prj1.board.exception.BoardNotFoundException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateMemberException.class)
    public String handleDuplicateMember(DuplicateMemberException e, RedirectAttributes rttr) {
        rttr.addFlashAttribute("alert", Map.of("code", "warning", "message", e.getMessage()));
        return "redirect:/member/signup";
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public String handleMemberNotFound(MemberNotFoundException e, RedirectAttributes rttr) {
        rttr.addFlashAttribute("alert", Map.of("code", "danger", "message", e.getMessage()));
        return "redirect:/member/list";
    }

    @ExceptionHandler(BoardNotFoundException.class)
    public String handleBoardNotFound(BoardNotFoundException e, RedirectAttributes rttr) {
        rttr.addFlashAttribute("alert", Map.of("code", "danger", "message", e.getMessage()));
        return "redirect:/board/list";
    }

    // 그 외 예상치 못한 예외는 여기에 추가 가능
}
