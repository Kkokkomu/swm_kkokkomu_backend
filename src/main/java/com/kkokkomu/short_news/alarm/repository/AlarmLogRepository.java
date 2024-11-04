package com.kkokkomu.short_news.alarm.repository;

import com.kkokkomu.short_news.alarm.domain.AlarmLog;
import com.kkokkomu.short_news.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmLogRepository extends JpaRepository<AlarmLog, Long> {
    Long countByReceiverAndIsReadFalse(User receiver);
}
