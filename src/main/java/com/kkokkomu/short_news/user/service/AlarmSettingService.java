package com.kkokkomu.short_news.user.service;

import com.kkokkomu.short_news.core.type.EAlarmType;
import com.kkokkomu.short_news.core.util.TimeUtil;
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

    private final TimeUtil timeUtil;

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
        if (!user.getNightAlarmYn() && timeUtil.isNight()) {
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

}
