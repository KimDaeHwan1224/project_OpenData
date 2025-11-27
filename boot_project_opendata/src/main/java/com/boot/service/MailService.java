package com.boot.service;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.boot.dao.UserDAO;
import com.boot.dto.UserDTO;


@Service
public class MailService {
	
	@Autowired
    private JavaMailSender mailSender; // 필드 주입으로 변경
	@Autowired
	private SqlSession sqlSession;

    private static final String senderEmail = "knnn4533@gmail.com"; // 본인 Gmail로 변경
    private static int number;

    // 이메일 중복 여부 확인
    public boolean isEmailRegistered(String email) {
        UserDAO dao = sqlSession.getMapper(UserDAO.class);
        int count = dao.checkEmail(email);
        return count > 0; // 1 이상이면 이미 존재
    }

    // 랜덤 숫자 생성
    public static void createNumber() {
        number = (int)(Math.random() * (90000)) + 100000;
    }

    // 메일 내용 생성
    public MimeMessage createMail(String mail) {
        createNumber();
        MimeMessage message = mailSender.createMimeMessage();
        

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증번호");

            String body = "";
            body += "<h3>요청하신 인증번호입니다.</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>감사합니다.</h3>";

            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    // 메일 전송
    public int sendMail(String mail) {
    	MimeMessage message = createMail(mail);
    	mailSender.send(message);
    	return number;
    }
    
    // 아이디 찾기용: 이메일로 user_id 조회 
    public String findUserIdByEmail(String email) {
        UserDAO dao = sqlSession.getMapper(UserDAO.class);
        return dao.findIdByEmail(email);
    }
    
 // 아이디 + 이메일 검증
    public boolean validateUserIdEmail(String userId, String email) {
        UserDAO dao = (UserDAO) sqlSession.getMapper(UserDAO.class);
        Map<String, String> param = new HashMap<String, String>();
        param.put("user_id", userId);
        param.put("user_email", email);

        String result = dao.findPwByIdEmail(param);
        return result != null;
    }

    // 비밀번호 재설정 토큰 저장
    public void saveResetToken(String userId, String token) {
        UserDAO dao = (UserDAO) sqlSession.getMapper(UserDAO.class);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userId);
        map.put("user_pwd_reset", token);
        dao.saveResetToken(map);
    }
    // 토큰으로 사용자 조회
    public UserDTO findUserByResetToken(String token) {
        UserDAO dao = sqlSession.getMapper(UserDAO.class);
        return dao.findUserByResetToken(token);
    }

    // 토큰으로 비밀번호 변경
    public boolean updatePasswordByToken(String token, String newPassword) {
        UserDAO dao = sqlSession.getMapper(UserDAO.class);
        Map<String, String> param = new HashMap<String, String>();
        param.put("user_pwd_reset", token);
        param.put("user_pw", newPassword);
        int result = dao.updatePasswordByToken(param);
        return result > 0;
    }
    // 일반 사용자 지정 메일 보내기
    public void sendCustomMail(String recipient, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, recipient);
            message.setSubject(subject);
            message.setText(htmlContent, "UTF-8", "html");
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
	// 🔥 관리자 OTP 발송용 (내용만 다름)
    public void sendAdminOTP(String email, int otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("관리자 OTP 인증번호");

            String html = "<h2>관리자 OTP 인증번호</h2>"
                    + "<p>아래 OTP 번호를 입력해주세요:</p>"
                    + "<h1>" + otp + "</h1>";

            message.setText(html, "UTF-8", "html");
            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    
}
