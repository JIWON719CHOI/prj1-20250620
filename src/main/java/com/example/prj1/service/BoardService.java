package com.example.prj1.service;

import com.example.prj1.entity.Board;
import com.example.prj1.repo.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.prj1.dto.BoardForm;


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
    public Page<Board> list(Integer page, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, 50, Sort.by("id").descending());
        return boardRepository.findByTitleContaining(keyword, pageable); // Page<Board> 반환
    }

}
