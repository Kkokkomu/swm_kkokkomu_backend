package com.kkokkomu.short_news.controller;

import com.kkokkomu.short_news.dto.common.ResponseDto;
import com.kkokkomu.short_news.dto.reaction.request.PostReactionDto;
import com.kkokkomu.short_news.service.ReactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reaction")
public class ReactionController {
    private final ReactionService reactionService;

    @PostMapping("")
    public ResponseDto<?> createReaction(@RequestBody PostReactionDto postReactionDto) {
        return ResponseDto.ok(reactionService.reaction(postReactionDto));
    }

    @PostMapping("/delete")
    public ResponseDto<?> deleteReaction(@RequestBody PostReactionDto postReactionDto) {
        return ResponseDto.ok(reactionService.deleteReaction(postReactionDto));
    }
}
