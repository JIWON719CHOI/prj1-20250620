package com.example.prj1.board.dto;

import com.example.prj1.board.entity.Board;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Board}
 */
//@Value
@Data
public class BoardDto implements Serializable {
    private Integer id;
    private String title;
    private String content;
    private String writer;    // 닉네임
    private String writerId; // 작성자 id 추가
    private LocalDateTime createdAt;
}