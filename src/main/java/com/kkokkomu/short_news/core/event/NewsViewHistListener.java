package com.kkokkomu.short_news.core.event;


import com.kkokkomu.short_news.news.domain.News;
import com.kkokkomu.short_news.news.domain.NewsViewHist;
import com.kkokkomu.short_news.news.repository.NewsRepository;
import jakarta.persistence.PostPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewsViewHistListener {
    @Autowired
    private NewsRepository newsRepository;

    @PostPersist
    public void postPersist(NewsViewHist newsViewHist) {
        News news = newsViewHist.getNews();
        news.incrementViewCnt();
        newsRepository.save(news);
    }
}