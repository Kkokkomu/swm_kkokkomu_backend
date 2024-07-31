package com.kkokkomu.short_news.dto.auth.request;

import com.kkokkomu.short_news.type.ELoginProvider;
import com.kkokkomu.short_news.type.ESex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
//f
public record SocialRegisterRequestDto(
        @NotNull ELoginProvider provider,

        @Size(min = 1, max = 10)
        @Pattern(regexp = "^(?=.*[가-힣A-Za-z])[가-힣A-Za-z\\s]*$", message = "올바른 닉네임 형식이 아닙니다.")
        @NotBlank String nickname,

        @NotNull ESex sex,

        @NotNull LocalDate birthday
) {
}
