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

    public String sendEmail(String to, String title, String content) {
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
}
