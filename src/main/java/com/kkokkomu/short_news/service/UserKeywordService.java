package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Keyword;
import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.domain.UserKeyword;
import com.kkokkomu.short_news.dto.userKeyword.request.CreateUserKeywordDto;
import com.kkokkomu.short_news.dto.userKeyword.request.RegisterUserKeyword;
import com.kkokkomu.short_news.dto.userKeyword.response.UserKeywordDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.KeywordRepository;
import com.kkokkomu.short_news.repository.UserKeywordRepository;
import com.kkokkomu.short_news.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserKeywordService {
    private final UserRepository userRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final KeywordRepository keywordRepository;

    private final KeywordService keywordService; // 이벤트 핸들링 필요

    public UserKeywordDto createUserKeyword(Long userId, CreateUserKeywordDto createUserKeywordDto) {
        log.info("createUserKeyword start");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));


        Keyword newKeyword = keywordService.createKeyword(createUserKeywordDto.keyword());

        UserKeyword userKeyword = userKeywordRepository.save(
                UserKeyword.builder()
                        .user(user)
                        .keyword(newKeyword)
                        .build()
        );

        return UserKeywordDto.of(userKeyword);
    } // 유저 키워드 생성 (새 키워드 생성)

    public UserKeywordDto registerUserKeyword(Long userId, RegisterUserKeyword registerUserKeyword) {
        log.info("registerUserKeyword start");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Keyword newKeyword = keywordRepository.findById(registerUserKeyword.keywordId())
                        .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_KEYWORD));

        // 이미 등록된 키워드인지 검
        if (userKeywordRepository.findByUserIdAndKeywordId(userId, registerUserKeyword.keywordId()).isPresent()) {
            throw new CommonException(ErrorCode.ALREADY_REGISTERED_KEYWORD);
        }

        UserKeyword userKeyword = userKeywordRepository.save(
                UserKeyword.builder()
                        .user(user)
                        .keyword(newKeyword)
                        .build()
        );

        return UserKeywordDto.of(userKeyword);
    } // 유저 키워드 생성 (기존 키워드 등록)

    public String deleteUserKeyword(Long userKeywordId) {
        log.info("deleteUserKeyword start");

        userKeywordRepository.findById(userKeywordId)
                        .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER_KEYWORD));

        userKeywordRepository.deleteById(userKeywordId);
        return "success";
    } // 유저 키워드 삭제

    @Transactional
    public List<UserKeywordDto> getUserKeywords(Long userId) {
        log.info("getUserKeywords start");

        List<UserKeyword> userKeywords = userKeywordRepository.findAllByUserId(userId);

        return UserKeywordDto.of(userKeywords);
    }
}
