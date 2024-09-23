package com.kkokkomu.short_news.user.dto.user.request;

import com.kkokkomu.short_news.core.type.ESex;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserDto(
        @Size(min = 1, max = 10)
        @Pattern(regexp = "^[0-9가-힣a-zA-Z]+$", message = "올바른 닉네임 형식이 아닙니다.")
        @NotNull String nickname,
        @NotNull LocalDate birthday,
        @NotNull ESex sex
) {
}
