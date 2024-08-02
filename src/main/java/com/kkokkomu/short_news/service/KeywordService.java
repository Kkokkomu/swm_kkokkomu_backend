package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Keyword;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordService {
    private final KeywordRepository keywordRepository;

    public Keyword createKeyword(String keyword) {
        log.info("Create keyword: {}", keyword);

        if (keywordRepository.findByKeyword(keyword).isPresent()) {
            throw new CommonException(ErrorCode.DUPLICATED_KEYWORD);
        }

        return keywordRepository.save(
                Keyword.builder()
                        .keyword(keyword)
                        .build()
        );
    } // 키워드 생성
}
