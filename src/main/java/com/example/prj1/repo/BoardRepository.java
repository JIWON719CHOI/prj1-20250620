package com.example.prj1.repo;

import com.example.prj1.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Integer> {
    Page<Board> findByTitleContaining(String keyword, Pageable pageable);
}