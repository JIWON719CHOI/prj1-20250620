package com.example.prj1.board.controller;

import com.example.prj1.board.dto.BoardForm;
import com.example.prj1.board.dto.BoardDto;
import com.example.prj1.board.service.BoardService;
import com.example.prj1.member.dto.MemberDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    // 글쓰기 폼
    @GetMapping("write")
    public String writeForm(HttpSession session, RedirectAttributes rttr) {
        if (session.getAttribute("loggedInUser") == null) {
            rttr.addFlashAttribute("alert", Map.of("code", "warning", "message", "로그인 후 글을 작성해주세요."));
            return "redirect:/member/login";
        }
        return "board/write";
    }

    // 글 작성 처리
    @PostMapping("write")
    public String writePost(BoardForm data, @SessionAttribute(value = "loggedInUser", required = false) MemberDto user, RedirectAttributes rttr) {
        boardService.add(data, user);
        rttr.addFlashAttribute("alert", Map.of("code", "primary", "message", "새 게시물이 등록되었습니다."));
        return "redirect:/board/list";
    }

    // 게시글 목록
    @GetMapping("list")
    public String list(Model model,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "") String keyword) {
        model.addAllAttributes(boardService.getListPage(page, keyword));
        return "board/list";
    }

    // 게시글 상세
    @GetMapping("detail")
    public String detail(@RequestParam("id") Integer id, Model model) {
        BoardDto dto = boardService.get(id);
        model.addAttribute("board", dto);
        return "board/detail";
    }

    // 게시글 삭제
    @PostMapping("remove")
    public String remove(Integer id, @SessionAttribute(value = "loggedInUser", required = false) MemberDto user, RedirectAttributes rttr) {
        boardService.removeOrThrow(id, user);
        rttr.addFlashAttribute("alert", Map.of("code", "danger", "message", id + "번 게시물이 삭제되었습니다."));
        return "redirect:/board/list";
    }

    // 게시글 수정 폼
    @GetMapping("edit")
    public String editForm(Integer id, Model model, @SessionAttribute("loggedInUser") MemberDto user) {
        BoardForm form = boardService.getFormOrThrow(id, user);
        model.addAttribute("board", form);
        return "board/edit";
    }

    // 게시글 수정 처리
    @PostMapping("edit")
    public String editPost(BoardForm data, @SessionAttribute("loggedInUser") MemberDto user, RedirectAttributes rttr) {
        boardService.updateOrThrow(data, user);
        rttr.addFlashAttribute("alert", Map.of("code", "success", "message", data.getId() + "번 게시물이 수정되었습니다."));
        rttr.addAttribute("id", data.getId());
        return "redirect:/board/detail";
    }
}
