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

import java.util.HashMap;
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

        var pageResult = boardService.list(page, keyword); // Page<Board>

        int currentPage = page;
        int totalPages = pageResult.getTotalPages();

        int leftPage = Math.max(1, currentPage - 2);
        int rightPage = Math.min(totalPages, currentPage + 2);
        int prevPage = Math.max(1, currentPage - 1);
        int nextPage = Math.min(totalPages, currentPage + 1);

        // 👇 param Map 생성
        // ⏱️ 간단함 : DTO 클래스 따로 안만들어도 됨.
        // 🔄 유연함 : key-value 쉽게 추가 가능.
        // 💬 템플릿에서 사용 편함 : param.keyword, param.page 그대로 접근 가능.
        // 💡 실무에서 흔히 사용 : 페이징, 검색 조건 등 여러 개 넘길 때 유용.
        Map<String, Object> param = new HashMap<>();
        param.put("keyword", keyword);
        param.put("page", currentPage);

        model.addAttribute("boardList", pageResult.getContent());
        model.addAttribute("param", param);
        model.addAttribute("leftPage", leftPage);
        model.addAttribute("rightPage", rightPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("lastPage", totalPages);

        return "board/list";
    }

//    http://localhost:8080/board/detail?id=4

    @GetMapping("detail")
    public String detail(@RequestParam("id") Integer id, Model model) {
        var board = boardService.get(id);
        model.addAttribute("board", board);
        return "board/detail";
    }


}
