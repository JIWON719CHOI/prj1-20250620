CREATE DATABASE prj1;
USE prj1;

# 게시물 테이블
CREATE TABLE board
(
    id         INT AUTO_INCREMENT NOT NULL,
    title      VARCHAR(255)       NOT NULL,
    content    VARCHAR(10000)     NOT NULL,
    writer     VARCHAR(120)       NOT NULL,
    created_at datetime           NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_board PRIMARY KEY (id)
);

# 페이징 용 글 복사
INSERT INTO board
    (title, content, writer)
SELECT title, content, writer
FROM board;

SELECT COUNT(*)
FROM board;

# 회원 정보
CREATE TABLE member
(
    id         VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NULL,
    nick_name  VARCHAR(255) NULL,
    info       VARCHAR(255) NULL,
    created_at datetime     NULL,
    CONSTRAINT pk_member PRIMARY KEY (id)
);

ALTER TABLE member
    MODIFY created_at DATETIME DEFAULT NOW();

# 회원만 글을 작성할 수 있으므로, board.writer를 member.id로 수정

UPDATE board
SET writer = 'Geto'
WHERE id % 2 = 1;

UPDATE board
SET writer = 'Gojo'
WHERE id % 2 = 0;

# 외래키 제약 사항 추가
ALTER TABLE board
    ADD FOREIGN KEY (writer) REFERENCES member (id);

ALTER TABLE board
    MODIFY writer VARCHAR(100) NOT NULL;

# 🔧 방법 1: 테이블을 직접 수정 (SQL로 컬럼 추가)
ALTER TABLE board
    ADD COLUMN writer_id VARCHAR(255);

UPDATE board
SET writer_id = 'Geto'
WHERE id % 2 = 1;

UPDATE board
SET writer_id = 'Gojo'
WHERE id % 2 = 0;
# writer_id 컬럼 NOT NULL 설정 (필요하면)
ALTER TABLE board
    MODIFY writer_id VARCHAR(255) NOT NULL;
# 외래키 제약 조건 추가
ALTER TABLE board
    ADD FOREIGN KEY (writer_id) REFERENCES member (id);


ALTER TABLE board
    DROP FOREIGN KEY board_ibfk_1;

ALTER TABLE board
    ADD CONSTRAINT board_ibfk_1 FOREIGN KEY (writer) REFERENCES member (id) ON DELETE SET NULL;
ALTER TABLE board
    MODIFY writer VARCHAR(255) NULL;

SHOW CREATE TABLE board;
# 1. 긍정적 사항
# writer 컬럼에 대해 외래키 제약조건이 걸려있고 ON DELETE SET NULL 옵션이 적용되어 있습니다.
# 즉, member가 삭제되면 writer 컬럼은 자동으로 NULL이 됩니다.
# writer 컬럼이 DEFAULT NULL로 nullable 상태입니다. 회원 탈퇴 후 게시물의 작성자 정보가 없어져도 DB 에러가 나지 않습니다.
# writer_id 컬럼이 있고, 외래키로 member.id를 참조합니다.
# 다만 writer_id는 NOT NULL 상태로 되어 있네요.

# 2. 개선 및 주의할 점
# (1) writer 와 writer_id 두 컬럼이 동시에 존재함
# 보통은 회원 탈퇴 문제 해결하려 writer 컬럼을 nullable 하고 ON DELETE SET NULL 옵션을 주거나,
# 혹은 writer_id 컬럼만 사용하고 게시물 작성자 닉네임 등 텍스트 정보를 따로 둡니다.
# 현재 두 컬럼이 모두 존재하면 혼란이 올 수 있으니 역할을 명확히 해야 합니다.
# (2) writer_id 가 NOT NULL인데 외래키 제약은 단순 REFERENCES 만 있음
# 회원 탈퇴 시 writer_id 값이 없어질 수 없어서 탈퇴 처리가 어려울 수 있습니다.
# 회원 탈퇴 가능하게 하려면 writer_id도 nullable 하거나 외래키 제약 조건에 ON DELETE SET NULL 또는 ON DELETE CASCADE 옵션을 넣는 게 좋습니다.
# (3) JPA Entity 매핑에서 주의점
# writer 컬럼과 writer_id 컬럼을 각각 @ManyToOne으로 매핑하면 안 되고,
# 한 컬럼을 외래키로 매핑하고 다른 컬럼은 단순 문자열 컬럼으로 두어야 합니다.







