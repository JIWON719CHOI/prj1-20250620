package com.example.prj1.board.service;

import com.example.prj1.board.dto.BoardDto;
import com.example.prj1.board.dto.BoardForm;
import com.example.prj1.board.entity.Board;
import com.example.prj1.board.exception.BoardNotFoundException;
import com.example.prj1.board.repo.BoardRepository;
import com.example.prj1.member.dto.MemberDto;
import com.example.prj1.member.entity.Member;
import com.example.prj1.member.repo.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    public void add(BoardForm formData, MemberDto user) {
        Board board = new Board();
        board.setTitle(formData.getTitle());
        board.setContent(formData.getContent());

        Member member = memberRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        board.setWriter(member); // FK
        board.setWriterNickName(member.getNickName()); // ✨ 닉네임 복사 저장

        boardRepository.save(board);
    }


    public Map<String, Object> getListPage(Integer page, String keyword) {
        Page<Board> pageResult = boardRepository.findByTitleContainingOrWriter_NickNameContaining(
                keyword, keyword,
                PageRequest.of(page - 1, 50, Sort.by("id").descending()));

        // Board 엔티티 리스트 → BoardDto 리스트 변환
        List<BoardDto> dtoList = pageResult.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        int currentPage = page;
        int totalPages = pageResult.getTotalPages();
        int blockSize = 10;
        int currentBlockStart = ((currentPage - 1) / blockSize) * blockSize + 1;

        Map<String, Object> param = new HashMap<>();
        param.put("page", currentPage);
        param.put("keyword", keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("boardList", dtoList);  // 엔티티가 아니라 DTO 리스트를 넣음
        result.put("param", param);
        result.put("leftPage", currentBlockStart);
        result.put("rightPage", Math.min(currentBlockStart + blockSize - 1, totalPages));
        result.put("prevPage", Math.max(1, currentBlockStart - blockSize));
        result.put("nextPage", Math.min(totalPages, currentBlockStart + blockSize));
        result.put("lastPage", totalPages);

        return result;
    }


    public BoardDto get(Integer id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(id + "번 게시물이 존재하지 않습니다."));
        return toDto(board);
    }

    public void removeOrThrow(Integer id, MemberDto user) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(id + "번 게시물이 존재하지 않습니다."));

        // 회원 정보에서 역할 가져오기 (필요하다면 memberRepository 호출해서 실제 Member 엔티티를 가져와도 됨)
        boolean isOwner = board.getWriter() != null && board.getWriter().getId().equals(user.getId());
        boolean isAdmin = "ADMIN".equals(user.getRole());  // user DTO에 role 필드가 있다고 가정

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        boardRepository.deleteById(id);
    }


    public void updateOrThrow(BoardForm data, MemberDto user) {
        Board board = boardRepository.findById(data.getId())
                .orElseThrow(() -> new BoardNotFoundException(data.getId() + "번 게시물이 존재하지 않습니다."));

        if (!board.getWriter().getId().equals(user.getId())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        board.setTitle(data.getTitle());
        board.setContent(data.getContent());

        boardRepository.save(board);
    }

    public BoardForm getFormOrThrow(Integer id, MemberDto user) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(id + "번 게시물이 존재하지 않습니다."));

        if (!board.getWriter().getId().equals(user.getId())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        BoardForm form = new BoardForm();
        form.setId(board.getId());
        form.setTitle(board.getTitle());
        form.setContent(board.getContent());
        form.setWriter(board.getWriterNickName()); // 여기서 작성자 닉네임 설정
        return form;
    }


    private BoardDto toDto(Board board) {
        BoardDto dto = new BoardDto();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setContent(board.getContent());

        // writerNickName 은 항상 값이 남아있고,
        // writer_id 가 null 이면 "(탈퇴)" 를 붙여 주면 됩니다.
        dto.setWriterNickName(board.getWriterNickName());
        dto.setWriterId(board.getWriter() != null
                ? board.getWriter().getId()
                : null);
        dto.setCreatedAt(board.getCreatedAt());
        return dto;
    }


}
