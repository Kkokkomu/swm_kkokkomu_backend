package com.kkokkomu.short_news.alarm.dto.fcm.request;

import com.kkokkomu.short_news.alarm.domain.Notification;
import com.kkokkomu.short_news.comment.domain.Comment;
import com.kkokkomu.short_news.core.type.EAlarmType;
import com.kkokkomu.short_news.user.domain.User;
import lombok.Builder;

@Builder
public record CreateAlarmLogDto(
        User receiver,
        EAlarmType alarmType,
        Comment comment,
        Notification notification
) {
}
