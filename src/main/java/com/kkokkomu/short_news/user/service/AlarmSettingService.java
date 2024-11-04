package com.kkokkomu.short_news.user.service;

import com.kkokkomu.short_news.core.type.EAlarmType;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.dto.user.request.UpdateAlarmSettingDto;
import com.kkokkomu.short_news.user.dto.user.response.UserDto;
import com.kkokkomu.short_news.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmSettingService {
    private final UserRepository userRepository;

    private final UserLookupService userLookupService;

    @Transactional
    public UserDto updateAlarmSetting(UpdateAlarmSettingDto updateAlarmSettingDto, Long userId) {
        User user = userLookupService.findUserById(userId);

        user.updateAlarmSetting(
                updateAlarmSettingDto.nightAlarmYn(),
                updateAlarmSettingDto.alarmNewContentYn(),
                updateAlarmSettingDto.alarmReplyYn(),
                updateAlarmSettingDto.alarmInformYn());

        userRepository.save(user);

        return UserDto.of(user);
    }

    public Boolean getReplySettingValid(User user) {
        if (!user.getNightAlarmYn() && isNight()) {
            log.info("night alarm Yn is false");
            return false;
        } else if (!user.getAlarmReplyYn()) {
            log.info("alarm reply Yn is false");
            return false;
        } else {
            log.info("alarm reply Yn is true");
            return true;
        }
    }

    private Boolean isNight() {
        LocalTime currentTime = LocalDateTime.now().toLocalTime();
        LocalTime startNightTime = LocalTime.of(21, 0); // 21:00 (밤 9시)
        LocalTime endNightTime = LocalTime.of(8, 0); // 08:00 (아침 8시)

        // 밤 시간은 저녁 9시부터 다음 날 아침 8시까지이므로 시간대 비교를 두 부분으로 나눕니다.
        return (currentTime.isAfter(startNightTime) || currentTime.equals(startNightTime))
                || currentTime.isBefore(endNightTime);
    }
}
