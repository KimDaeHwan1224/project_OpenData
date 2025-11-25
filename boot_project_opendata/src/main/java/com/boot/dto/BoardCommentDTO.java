package com.boot.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class BoardCommentDTO {
    private Long commentNo;           // 댓글 번호 (PK)
    private Long boardNo;             // 게시글 번호 (FK)
    private String userId;            // 작성자
    private String commentContent;    // 댓글 내용
    private Long parentCommentNo;     // 부모 댓글 번호 (대댓글용, NULL이면 원댓글)
    private String isDeleted;         // 삭제 여부 ('Y' or 'N')
    
    // ✅ DB: TIMESTAMP → JSON 직렬화 시 형식 지정
    @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd HH:mm",
                timezone = "Asia/Seoul")
    private LocalDateTime commentDate;  // 작성일시
    
    private String userNickname;        // 작성자 닉네임 (JOIN용)
    
    // ✅ 화면(JSP) 표시용 포맷 메서드
    @JsonIgnore
    public String getFormattedDate() {
        if (commentDate == null) return "";
        return commentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    // ✅ 대댓글 여부 확인
    @JsonIgnore
    public boolean isReply() {
        return parentCommentNo != null;
    }
}