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

        // 키워드가 2글자이상 20글자이하 알파벳/한글/숫자로 구성된 한 단어로 구성됐는지 검사
        if (!keyword.matches("^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9]{2,20}$")) {
            throw new CommonException(ErrorCode.INVALID_KEYWORD);
        }

        // 같은 키워드가 이미 등록되어 있는지 검사
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
