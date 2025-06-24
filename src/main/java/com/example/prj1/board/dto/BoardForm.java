
package com.example.prj1.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BoardForm {
    private Integer id;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    // 작성자 닉네임 (readonly 표시용)
    private String writer;
    // 작성자 정보는 절대 클라이언트가 수정하거나 입력하게 하지 않습니다.
    // 따라서 여기서 writer 필드는 빼고, 서버에서 세션 유저로 처리하세요.
}
