package com.example.prj1.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberForm {
    @NotBlank(message = "아이디를 입력해주세요.")
    private String id;
    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickName;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
    private String oldPassword;   // changePw용
    private String newPassword;
    private String info;
}