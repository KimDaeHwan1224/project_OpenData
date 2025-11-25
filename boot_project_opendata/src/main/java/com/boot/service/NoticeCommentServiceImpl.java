package com.boot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boot.dao.NoticeCommentDAO;
import com.boot.dto.NoticeCommentDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeCommentServiceImpl implements NoticeCommentService {

    private final NoticeCommentDAO commentDAO;

    @Override
    @Transactional
    public Long write(NoticeCommentDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("댓글 데이터가 비었습니다.");
        }
        if (dto.getNoticeNo() == null) {
            throw new IllegalArgumentException("공지사항 번호(noticeNo)는 필수입니다.");
        }
        if (dto.getUserId() == null || dto.getUserId().isBlank()) {
            throw new IllegalArgumentException("작성자(userId)는 필수입니다.");
        }
        if (dto.getCommentContent() == null || dto.getCommentContent().isBlank()) {
            throw new IllegalArgumentException("댓글 내용(commentContent)은 필수입니다.");
        }
        
        commentDAO.insert(dto);
        return dto.getCommentNo();
    }

    @Override
    public List<NoticeCommentDTO> getCommentsByNoticeNo(Long noticeNo) {
        if (noticeNo == null) {
            throw new IllegalArgumentException("공지사항 번호(noticeNo)는 필수입니다.");
        }
        return commentDAO.selectByNoticeNo(noticeNo);
    }

    @Override
    public List<NoticeCommentDTO> getParentCommentsByNoticeNo(Long noticeNo, int page, int size) {
        if (noticeNo == null) {
            throw new IllegalArgumentException("공지사항 번호(noticeNo)는 필수입니다.");
        }
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        
        int offset = (page - 1) * size;
        return commentDAO.selectParentCommentsByNoticeNo(noticeNo, offset, size);
    }

    @Override
    public List<NoticeCommentDTO> getAllCommentsByNoticeNo(Long noticeNo, int page, int size) {
        if (noticeNo == null) {
            throw new IllegalArgumentException("공지사항 번호(noticeNo)는 필수입니다.");
        }
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        
        int offset = (page - 1) * size;
        return commentDAO.selectAllCommentsByNoticeNo(noticeNo, offset, size);
    }

    @Override
    public int getParentCommentCount(Long noticeNo) {
        if (noticeNo == null) {
            return 0;
        }
        return commentDAO.countParentCommentsByNoticeNo(noticeNo);
    }

    @Override
    public List<NoticeCommentDTO> getRepliesByParentNo(Long parentCommentNo) {
        if (parentCommentNo == null) {
            throw new IllegalArgumentException("부모 댓글 번호(parentCommentNo)는 필수입니다.");
        }
        return commentDAO.selectRepliesByParentNo(parentCommentNo);
    }

    @Override
    public NoticeCommentDTO find(Long commentNo) {
        if (commentNo == null) {
            throw new IllegalArgumentException("댓글 번호(commentNo)는 필수입니다.");
        }
        return commentDAO.find(commentNo);
    }

    @Override
    @Transactional
    public void update(NoticeCommentDTO dto) {
        if (dto == null || dto.getCommentNo() == null) {
            throw new IllegalArgumentException("댓글 번호(commentNo)는 필수입니다.");
        }
        if (dto.getCommentContent() == null || dto.getCommentContent().isBlank()) {
            throw new IllegalArgumentException("댓글 내용(commentContent)은 필수입니다.");
        }
        commentDAO.update(dto);
    }

    @Override
    @Transactional
    public void delete(Long commentNo) {
        if (commentNo == null) {
            throw new IllegalArgumentException("댓글 번호(commentNo)는 필수입니다.");
        }
        commentDAO.delete(commentNo);
    }

    @Override
    public int getCommentCount(Long noticeNo) {
        if (noticeNo == null) {
            return 0;
        }
        return commentDAO.countByNoticeNo(noticeNo);
    }

    @Override
    public String getUserIdByCommentNo(Long commentNo) {
        if (commentNo == null) {
            return null;
        }
        return commentDAO.getUserIdByCommentNo(commentNo);
    }
}