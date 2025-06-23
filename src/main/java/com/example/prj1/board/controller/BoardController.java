package com.example.prj1.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.example.prj1.board.dto.BoardForm;
import com.example.prj1.board.service.BoardService;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("board")
public class BoardController {

    private final BoardService boardService;

    // http://localhost:8080/board/write

    @GetMapping("write")
    public String writeForm() {

        return "board/write";
    }

    @PostMapping("write")
    public String writePost(@Valid BoardForm data,
                            BindingResult bindingResult, RedirectAttributes rttr) {
        if (bindingResult.hasErrors()) {
            // 유효성 실패 -> 에러 메시지를 flash에 담아서 다시 폼으로
            rttr.addFlashAttribute("errors", bindingResult.getAllErrors());
            rttr.addFlashAttribute("alert",
                    Map.of("code", "danger", "message", "필수 항목을 모두 입력해주세요."));
            return "redirect:/board/write";
        }

        boardService.add(data);
        rttr.addFlashAttribute("alert",
                Map.of("code", "primary", "message", "새 게시물이 등록되었습니다."));
        return "redirect:/board/list";
    }

    // http://localhost:8080/board/list

    @GetMapping("list")
    public String list(Model model,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "") String keyword) {

        Map<String, Object> result = boardService.getListPage(page, keyword);
        model.addAllAttributes(result);

        return "board/list";
    }

    // http://localhost:8080/board/detail?id=4

    @GetMapping("detail")
    public String detail(@RequestParam("id") Integer id, Model model) {
        var board = boardService.get(id);
        model.addAttribute("board", board);
        return "board/detail";
    }

    @PostMapping("remove")
    public String remove(Integer id, RedirectAttributes rttr) {
        boardService.remove(id);

        rttr.addFlashAttribute("alert",
                Map.of("code", "danger", "message", id + "번 게시물이 삭제 되었습니다."));

        return "redirect:/board/list";
    }

    @GetMapping("edit")
    public String edit(Integer id, Model model) {
        var dto = boardService.get(id);
        model.addAttribute("board", dto);
        return "board/edit";
    }

    @PostMapping("edit")
    public String editPost(BoardForm data, RedirectAttributes rttr) {
        boardService.update(data);

        rttr.addFlashAttribute("alert",
                Map.of("code", "success", "message",
                        data.getId() + "번 게시물이 수정되었습니다."));

        rttr.addAttribute("id", data.getId());

        return "redirect:/board/detail";
    }


}
