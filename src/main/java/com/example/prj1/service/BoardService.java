package com.example.prj1.service;

import com.example.prj1.dto.BoardForm;
import com.example.prj1.dto.BoardListInfo;
import com.example.prj1.entity.Board;
import com.example.prj1.repo.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<BoardListInfo> list(Integer page) {
        List<Board> list = boardRepository.findAll();
        // 나는 현재 4095의 행이 있고 50개씩 보여주면 마지막 페이지 번호는 82번 이다.
        return boardRepository.findAllBy(PageRequest.of(page - 1, 50, Sort.by("id").descending()));
    }
}
