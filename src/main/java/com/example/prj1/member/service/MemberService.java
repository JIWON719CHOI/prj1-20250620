package com.example.prj1.member.service;

import com.example.prj1.member.dto.MemberDto;
import com.example.prj1.member.dto.MemberForm;
import com.example.prj1.member.dto.MemberListInfo;
import com.example.prj1.member.entity.Member;
import com.example.prj1.member.exception.DuplicateMemberException;
import com.example.prj1.member.exception.MemberNotFoundException;
import com.example.prj1.member.repo.MemberRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public void add(MemberForm data) {
        if (memberRepository.findById(data.getId()).isPresent()) {
            throw new DuplicateMemberException(data.getId() + "는 이미 있는 아이디입니다.");
        }
        if (memberRepository.findByNickName(data.getNickName()).isPresent()) {
            throw new DuplicateMemberException(data.getNickName() + "는 이미 있는 별명입니다.");
        }
        Member member = new Member();
        member.setId(data.getId());
        member.setPassword(data.getPassword());
        member.setNickName(data.getNickName());
        member.setInfo(data.getInfo());

        memberRepository.save(member);
    }

    public Member get(String id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("회원이 없습니다."));
    }

    public boolean remove(MemberForm data) {
        Member member = get(data.getId());
        if (member.getPassword().equals(data.getPassword())) {
            memberRepository.delete(member);
            return true;
        } else {
            return false;
        }
    }

    public boolean update(MemberForm data, MemberDto user, HttpSession session) {
        Member member = get(data.getId());

        if (!member.getPassword().equals(data.getPassword())) {
            return false;
        }

        // 닉네임, 소개 갱신
        member.setNickName(data.getNickName());
        member.setInfo(data.getInfo());
        memberRepository.save(member);

        // 세션에 저장된 로그인 정보도 업데이트
        user.setNickName(data.getNickName());
        user.setInfo(data.getInfo());
        session.setAttribute("loggedInUser", user);

        return true;
    }


    public boolean changePassword(String id, String oldPw, String newPw) {
        Member member = get(id);
        if (!member.getPassword().equals(oldPw)) {
            return false;
        }
        member.setPassword(newPw);
        memberRepository.save(member);
        return true;
    }

    // ✅ 회원 목록 조회
    public List<MemberListInfo> list() {
        return memberRepository.findAllBy();
    }

    // ✅ 로그인
    public boolean login(String id, String password, HttpSession session) {
        Optional<Member> db = memberRepository.findById(id);

        if (db.isPresent()) {
            String dbPassword = db.get().getPassword();
            if (dbPassword.equals(password)) {

                // memberDto를 session 에 넣기
                MemberDto dto = new MemberDto();
                dto.setId(db.get().getId());
                dto.setNickName(db.get().getNickName());
                dto.setInfo(db.get().getInfo());
                dto.setCreatedAt(db.get().getCreatedAt());

                session.setAttribute("loggedInUser", dto);
                return true;
            }
        }
        return false;
    }
}
