package com.kkokkomu.short_news.user.service;

import com.kkokkomu.short_news.core.config.service.MailService;
import com.kkokkomu.short_news.core.config.service.RedisService;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.util.RandomCodeUtil;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.dto.validateUser.EmailValicate;
import com.kkokkomu.short_news.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidateUserService {
    private final UserRepository userRepository;

    private final RedisService redisService;
    private final MailService mailService;

    public String sendValidateCodeByEmail(EmailValicate emailValicate) {
        log.info(emailValicate.email());
        User user = userRepository.findByEmail(emailValicate.email())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        String authCode = RandomCodeUtil.generateVerificationCode();
        mailService.sendCodeMail(emailValicate.email(), "[NEWSnack] 이메일 인증", authCode);

        redisService.saveCodeWithUserId(authCode, user.getId());
        log.info(authCode);

        return emailValicate.email();
    }

    public String validateUser(String authCode) {
        log.info("validateUser service");
        Long userId = redisService.getUserIdByCode(authCode);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        log.info("delete user {}", userId.toString());
        user.softDelete();

        userRepository.save(user);

        return authCode;
    }
}
