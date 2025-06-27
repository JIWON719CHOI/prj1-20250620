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


ALTER TABLE board
    DROP FOREIGN KEY board_ibfk_1;

ALTER TABLE board
    MODIFY writer VARCHAR(255) NOT NULL;

ALTER TABLE board
    MODIFY writer_id VARCHAR(255) NULL;

ALTER TABLE board
    DROP FOREIGN KEY board_ibfk_2;

ALTER TABLE board
    ADD CONSTRAINT board_ibfk_2 FOREIGN KEY (writer_id) REFERENCES member (id) ON DELETE SET NULL;

ALTER TABLE member
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- writerNickName 컬럼이 실제 DB에 존재할 때만 가능
ALTER TABLE board
    ADD COLUMN writer_nick_name VARCHAR(100) NOT NULL DEFAULT '';

UPDATE board b
    JOIN member m ON b.writer_id = m.id
SET b.writer_nick_name = m.nick_name
WHERE b.writer_nick_name = '';

ALTER TABLE board
    DROP FOREIGN KEY board_ibfk_1, -- 만약 존재한다면
    DROP COLUMN writer;

ALTER TABLE board
    -- 1) writer 컬럼에 걸려 있는 인덱스 제거
    DROP INDEX board_ibfk_1,
    -- 2) 이제 writer 컬럼 제거
    DROP COLUMN writer;



SHOW CREATE TABLE board;

SHOW CREATE TABLE member;




















