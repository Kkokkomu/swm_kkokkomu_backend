package com.kkokkomu.short_news.user.service;

import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.dto.user.request.UpdateAlarmSettingDto;
import com.kkokkomu.short_news.user.dto.user.response.UserDto;
import com.kkokkomu.short_news.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmSettingService {
    private final UserRepository userRepository;

    private final UserLookupService userLookupService;

    public UserDto updateAlarmSetting(UpdateAlarmSettingDto updateAlarmSettingDto, Long userId) {
        User user = userLookupService.findUserById(userId);

        user.updateAlarmSetting(
                updateAlarmSettingDto.nightAlarmYn(),
                user.getAlarmNewContentYn(),
                updateAlarmSettingDto.alarmReplyYn(),
                updateAlarmSettingDto.alarmBannedYn());

        userRepository.save(user);

        return UserDto.of(user);
    }
}
