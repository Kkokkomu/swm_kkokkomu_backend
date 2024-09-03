package com.kkokkomu.short_news.keyword.service;

import com.kkokkomu.short_news.keyword.domain.Keyword;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.keyword.domain.UserKeyword;
import com.kkokkomu.short_news.keyword.dto.userKeyword.request.CreateUserKeywordDto;
import com.kkokkomu.short_news.keyword.dto.userKeyword.request.RegisterUserKeyword;
import com.kkokkomu.short_news.keyword.dto.userKeyword.response.UserKeywordDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.keyword.repository.UserKeywordRepository;
import com.kkokkomu.short_news.user.service.UserLookupService;
import com.kkokkomu.short_news.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserKeywordService {
    private final UserKeywordRepository userKeywordRepository;

    private final UserLookupService userLookupService;
    private final KeywordService keywordService; // 이벤트 핸들링 필요

    public UserKeywordDto createUserKeyword(Long userId, CreateUserKeywordDto createUserKeywordDto) {
        log.info("createUserKeyword start");
        User user = userLookupService.findUserById(userId);

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
        User user = userLookupService.findUserById(userId);

        Keyword newKeyword = keywordService.getKeywordById(registerUserKeyword.keywordId());

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
