package com.kkokkomu.short_news.user.dto.user.response;

import com.kkokkomu.short_news.user.domain.User;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String profileUrl,
        String nickname,
        String email,
        String sex,
        String birthday,
        String createdAt,
        String editedAt,
        String profileEditedAt,
        Boolean nightAlarmYn,
        Boolean alarmNewContentYn,
        Boolean alarmReplyYn,
        Boolean alarmBannedYn
) {
    public static UserDto of(User user) {
        return UserDto.builder()
                .id(user.getId())
                .profileUrl(user.getProfileImgs().get(0).getImgUrl())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .sex(user.getSex().toString())
                .birthday(user.getBirthday().toString())
                .createdAt(user.getCreatedAt().toString())
                .editedAt(user.getEditedAt().toString())
                .profileEditedAt(user.getProfileImgs().get(0).getEditedAt().toString())
                .nightAlarmYn(user.getNightAlarmYn())
                .alarmNewContentYn(user.getAlarmNewContentYn())
                .alarmReplyYn(user.getAlarmReplyYn())
                .alarmBannedYn(user.getAlarmInformYn())
                .build();
    }
}
