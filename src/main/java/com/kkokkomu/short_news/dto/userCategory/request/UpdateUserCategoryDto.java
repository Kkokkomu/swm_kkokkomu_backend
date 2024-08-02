package com.kkokkomu.short_news.dto.userCategory.request;

import jakarta.validation.constraints.NotNull;

public record UpdateUserCategoryDto(
        @NotNull Boolean politics,
        @NotNull Boolean economy,
        @NotNull Boolean social,
        @NotNull Boolean entertain,
        @NotNull Boolean sports,
        @NotNull Boolean living,
        @NotNull Boolean world,
        @NotNull Boolean it
) {
}

//POLITICS("POLITICS"),
//ECONOMY("ECONOMY"),
//SOCIAL("SOCIAL"),
//ENTERTAIN("ENTERTAIN"),
//SPORTS("SPORTS");