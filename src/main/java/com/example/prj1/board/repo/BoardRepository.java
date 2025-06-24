package com.example.prj1.board.repo;

import com.example.prj1.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Integer> {
    // 제목 + 작성자 닉네임으로 검색
    Page<Board> findByTitleContainingOrWriter_NickNameContaining(String titleKeyword, String writerKeyword, Pageable pageable);
}