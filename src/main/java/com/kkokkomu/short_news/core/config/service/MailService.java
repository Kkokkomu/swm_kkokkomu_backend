package com.kkokkomu.short_news.core.config.service;

import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Slf4j
@Service
public class MailService {
    private final JavaMailSender emailSender;

    public String sendNewsMail(String to, String title, String content) {
        try {
            log.info("send email to " + to);
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(title);

            // HTML 형식으로 메일 내용 설정
            String htmlContent = "<h3>" + LocalDate.now().toString() + " kkm 뉴스" + "</h3>" +
                     content;
            helper.setText(htmlContent, true); // true로 설정, HTML을 사용 가능

            emailSender.send(message);
            System.out.println("Email sent successfully to " + to);
            return "success";
        } catch (MessagingException e) {
            throw new CommonException(ErrorCode.MAIL_SEND_ERROR);
        }
    }

    public void sendCodeMail(String to, String title, String authCode) {
        try {
            log.info("send email to " + to);
            log.info("authCode: " + authCode);
            log.info("title: " + title);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(title);

            // HTML 형식으로 메일 내용 설정
            String htmlContent = "<h3 style='font-size: 24px; font-weight: bold;'>" + "정말로 탈퇴하시겠어요?" + "</h3>" +
                    "<p>탈퇴 후에는 서비스 이용에 대한 모든 권리가 소멸됩니다.</p>" +
                    "<p>탈퇴 신청 후, 30일 이후에 계정 삭제가 완료되며 복구 및 동일 이메일로 재가입이 불가능합니다.</p>" +
                    "<p>작성한 댓글의 닉네임이 “알수없음”으로 표시됩니다.</p>" +
                    "<a href='http://kkm-shortnews.shop/validate/" + authCode + "' " +
                    "style='background-color: #4CAF50; color: white; padding: 10px 20px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px;'>" +
                    "탈퇴하기</a>";

            helper.setText(htmlContent, true); // true로 설정하여 HTML 사용 가능

            emailSender.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (MessagingException e) {
            throw new CommonException(ErrorCode.MAIL_SEND_ERROR);
        }
    }
}
