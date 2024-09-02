package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.Keyword;
import com.kkokkomu.short_news.dto.common.PageInfoDto;
import com.kkokkomu.short_news.dto.common.PagingResponseDto;
import com.kkokkomu.short_news.dto.keyword.response.SearchKeywordDto;
import com.kkokkomu.short_news.exception.CommonException;
import com.kkokkomu.short_news.exception.ErrorCode;
import com.kkokkomu.short_news.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public Keyword createKeywordOrNull(String newsKeyword) {
        log.info("Create keyword or Null: {}", newsKeyword);

        // 키워드가 2글자이상 20글자이하 알파벳/한글/숫자로 구성된 한 단어로 구성됐는지 검사
        if (!newsKeyword.matches("^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9]{2,20}$")) {
            return null;
        }

        Keyword keyword = keywordRepository.findByKeyword(newsKeyword).orElse(null);

        // 같은 이름의 키워드가 없다면
        if (keyword == null) {
            // 키워드 등록
            keyword = keywordRepository.save(
                    Keyword.builder()
                            .keyword(newsKeyword)
                            .build()
            );
        }

        return keyword;
    } // 키워드 생성 유효하지 않으면 null 반환

    public PagingResponseDto<List<SearchKeywordDto>> searchKeyword(String keyword, int page, int size) {
        LocalDateTime lastWeek = LocalDateTime.now().minusWeeks(1);
        Page<Object[]> results = keywordRepository.findPopularKeywords(keyword, lastWeek, PageRequest.of(page, size));

        List<SearchKeywordDto> searchKeywordDtos = new ArrayList<>();
        for (Object[] result : results) {
            searchKeywordDtos.add(
                    SearchKeywordDto.builder()
                            .keywordId(((Number) result[0]).longValue())
                            .keyword((String) result[1])
                            .usedCnt(((Number) result[2]).longValue())
                            .build()
            );
        }

        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(results);

        return PagingResponseDto.fromEntityAndPageInfo(searchKeywordDtos, pageInfoDto);
    } // 키워드 검색

    public Keyword getKeywordById(Long id) {
        return keywordRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_KEYWORD));
    }
}
