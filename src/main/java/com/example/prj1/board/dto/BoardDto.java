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
public class BoardDto {
    private Integer id;
    private String title;
    private String content;
    private String writerNickName; // 보존용 텍스트
    private String writerId;       // 실제 회원 id, 탈퇴하면 null
    private LocalDateTime createdAt;
}

