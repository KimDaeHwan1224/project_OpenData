package com.boot.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.boot.dto.NoticeCommentDTO;

@Mapper
public interface NoticeCommentDAO {
    
    /** ✅ 댓글 등록 */
    int insert(NoticeCommentDTO dto);
    
    /** ✅ 공지사항별 댓글 목록 조회 (부모 댓글 먼저, 대댓글은 부모 아래) */
    List<NoticeCommentDTO> selectByNoticeNo(@Param("noticeNo") Long noticeNo);
    
    /** ✅ 공지사항별 원댓글 목록 조회 (페이징) */
    List<NoticeCommentDTO> selectParentCommentsByNoticeNo(
        @Param("noticeNo") Long noticeNo,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
    
    /** ✅ 공지사항별 전체 댓글 목록 조회 (페이징, 최신순) */
    List<NoticeCommentDTO> selectAllCommentsByNoticeNo(
        @Param("noticeNo") Long noticeNo,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
    
    /** ✅ 공지사항별 원댓글 개수 */
    int countParentCommentsByNoticeNo(@Param("noticeNo") Long noticeNo);
    
    /** ✅ 특정 원댓글의 모든 답글 조회 */
    List<NoticeCommentDTO> selectRepliesByParentNo(@Param("parentCommentNo") Long parentCommentNo);
    
    /** ✅ 댓글 단건 조회 */
    NoticeCommentDTO find(@Param("commentNo") Long commentNo);
    
    /** ✅ 댓글 수정 */
    int update(NoticeCommentDTO dto);
    
    /** ✅ 댓글 삭제 (소프트 삭제) */
    int delete(@Param("commentNo") Long commentNo);
    
    /** ✅ 공지사항별 댓글 개수 */
    int countByNoticeNo(@Param("noticeNo") Long noticeNo);
    
    /** ✅ 댓글 작성자 확인 */
    String getUserIdByCommentNo(@Param("commentNo") Long commentNo);
}