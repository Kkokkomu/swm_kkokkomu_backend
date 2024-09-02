package com.kkokkomu.short_news.news.dto.newsReaction.response;

import lombok.Builder;

@Builder
public record NewReactionByUserDto(
        Boolean like,
        Boolean surprise,
        Boolean sad,
        Boolean angry
) {
//    static public NewReactionByUserDto fromNewsReaction(List<NewsReaction> newsReactions) {
//        boolean like = false;
//        boolean surprise = false;
//        boolean sad = false;
//        boolean angry = false;
//
//        for (NewsReaction newsReaction : newsReactions) {
//            switch (newsReaction.getReaction()) {
//                case LIKE:
//                    like = true;
//                    break;
//                case SURPRISE:
//                    surprise = true;
//                    break;
//                case SAD:
//                    sad = true;
//                    break;
//                case ANGRY:
//                    angry = true;
//                    break;
//            }
//        }
//
//        return NewReactionByUserDto.builder()
//                .like(like)
//                .surprise(surprise)
//                .sad(sad)
//                .angry(angry)
//                .build();
//    }
}
