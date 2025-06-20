package com.example.prj1.service;

import com.example.prj1.entity.Board;
import com.example.prj1.repo.BoardRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.prj1.dto.BoardForm;

import java.util.HashMap;
import java.util.Map;


@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public void add(BoardForm formData) {
        Board board = new Board();
        board.setTitle(formData.getTitle());
        board.setContent(formData.getContent());
        board.setWriter(formData.getWriter());

        boardRepository.save(board);
    }

    // 나는 현재 4095의 행이 있고 50개씩 보여준다.
    public Map<String, Object> getListPage(Integer page, String keyword) {
        Page<Board> pageResult = boardRepository.findByTitleContaining(keyword,
                PageRequest.of(page - 1, 50, Sort.by("id").descending()));

        int currentPage = page;
        int totalPages = pageResult.getTotalPages();
        int blockSize = 10;

        int currentBlockStart = ((currentPage - 1) / blockSize) * blockSize + 1;
        int leftPage = currentBlockStart;
        int rightPage = Math.min(currentBlockStart + blockSize - 1, totalPages);

        int prevPage = Math.max(1, currentBlockStart - blockSize);
        int nextPage = Math.min(totalPages, currentBlockStart + blockSize);

        Map<String, Object> param = new HashMap<>();
        param.put("page", currentPage);
        param.put("keyword", keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("boardList", pageResult.getContent());
        result.put("param", param);
        result.put("leftPage", leftPage);
        result.put("rightPage", rightPage);
        result.put("prevPage", prevPage);
        result.put("nextPage", nextPage);
        result.put("lastPage", totalPages);

        return result;
    }

    public Board get(Integer id) {
        return boardRepository.findById(id).orElseThrow();
    }

}
