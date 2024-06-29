package com.kkokkomu.short_news.dto.reaction.response;

import com.kkokkomu.short_news.domain.Reaction;
import com.kkokkomu.short_news.dto.common.ResponseDto;
import lombok.Builder;

@Builder
public record ReactionDto (
        Long userId,
        Long newsId,
        Boolean great,
        Boolean hate,
        Boolean expect,
        Boolean surprise,
        String createdAt
){
    static public ReactionDto fromEntity(Reaction reaction) {
        return ReactionDto.builder()
                .userId(reaction.getUser().getId())
                .newsId(reaction.getNews().getId())
                .great(reaction.isGreat())
                .hate(reaction.isHate())
                .expect(reaction.isExpect())
                .surprise(reaction.isSurprise())
                .createdAt(reaction.getCreatedAt().toString())
                .build();
    }
}
