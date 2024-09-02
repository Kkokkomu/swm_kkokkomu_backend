package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.News;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
public interface NewsLookupService {
    News findNewsById(Long newsId);

    Boolean existNewsById(Long newsId);
}
