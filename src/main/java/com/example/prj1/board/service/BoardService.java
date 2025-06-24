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
import java.util.Map;

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
        board.setWriter(member);

        boardRepository.save(board);
    }

    public Map<String, Object> getListPage(Integer page, String keyword) {
        Page<Board> pageResult = boardRepository.findByTitleContaining(keyword,
                PageRequest.of(page - 1, 50, Sort.by("id").descending()));

        int currentPage = page;
        int totalPages = pageResult.getTotalPages();
        int blockSize = 10;
        int currentBlockStart = ((currentPage - 1) / blockSize) * blockSize + 1;

        Map<String, Object> param = new HashMap<>();
        param.put("page", currentPage);
        param.put("keyword", keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("boardList", pageResult.getContent());
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

        if (!board.getWriter().getId().equals(user.getId())) {
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
        return form;
    }

    private BoardDto toDto(Board board) {
        BoardDto dto = new BoardDto();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setContent(board.getContent());
        dto.setWriterId(board.getWriter() != null ? board.getWriter().getId() : null);
        dto.setWriter(board.getWriter() != null ? board.getWriter().getNickName() : "알 수 없음");
        dto.setCreatedAt(board.getCreatedAt());
        return dto;
    }
}
