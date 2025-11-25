package com.boot.service;

import java.util.List;

import com.boot.dto.NoticeCommentDTO;

public interface NoticeCommentService {
    
    /** ✅ 댓글 작성 */
    Long write(NoticeCommentDTO dto);
    
    /** ✅ 공지사항별 댓글 목록 조회 */
    List<NoticeCommentDTO> getCommentsByNoticeNo(Long noticeNo);
    
    /** ✅ 공지사항별 원댓글 목록 조회 (페이징) */
    List<NoticeCommentDTO> getParentCommentsByNoticeNo(Long noticeNo, int page, int size);
    
    /** ✅ 공지사항별 전체 댓글 목록 조회 (페이징) */
    List<NoticeCommentDTO> getAllCommentsByNoticeNo(Long noticeNo, int page, int size);
    
    /** ✅ 공지사항별 원댓글 개수 */
    int getParentCommentCount(Long noticeNo);
    
    /** ✅ 특정 원댓글의 모든 답글 조회 */
    List<NoticeCommentDTO> getRepliesByParentNo(Long parentCommentNo);
    
    /** ✅ 댓글 단건 조회 */
    NoticeCommentDTO find(Long commentNo);
    
    /** ✅ 댓글 수정 */
    void update(NoticeCommentDTO dto);
    
    /** ✅ 댓글 삭제 */
    void delete(Long commentNo);
    
    /** ✅ 공지사항별 댓글 개수 */
    int getCommentCount(Long noticeNo);
    
    /** ✅ 댓글 작성자 확인 */
    String getUserIdByCommentNo(Long commentNo);
}