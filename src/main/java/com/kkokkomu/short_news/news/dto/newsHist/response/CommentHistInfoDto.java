package com.kkokkomu.short_news.news.dto.newsHist.response;

import com.kkokkomu.short_news.comment.dto.comment.response.CommentDto;
import com.kkokkomu.short_news.news.dto.news.response.NewsInfoDto;
import lombok.Builder;

@Builder
public record CommentHistInfoDto(
        CommentDto comment,
        NewsInfoDto news
) {
}
