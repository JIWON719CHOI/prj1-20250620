package com.example.prj1.controller;

import lombok.RequiredArgsConstructor;

import com.example.prj1.dto.BoardForm;
import com.example.prj1.service.BoardService;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String writePost(BoardForm data) {
        boardService.add(data);
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


}
