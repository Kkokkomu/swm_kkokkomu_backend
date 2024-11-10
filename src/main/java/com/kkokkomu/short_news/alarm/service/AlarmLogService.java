package com.kkokkomu.short_news.alarm.service;

import com.kkokkomu.short_news.alarm.domain.AlarmLog;
import com.kkokkomu.short_news.alarm.dto.AlarmLog.require.UpdateAlarmLogDto;
import com.kkokkomu.short_news.alarm.dto.AlarmLog.response.AlarmLogDto;
import com.kkokkomu.short_news.alarm.dto.fcm.request.CreateAlarmLogDto;
import com.kkokkomu.short_news.alarm.repository.AlarmLogRepository;
import com.kkokkomu.short_news.core.dto.CursorInfoDto;
import com.kkokkomu.short_news.core.dto.CursorResponseDto;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.service.UserLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmLogService {
    private final AlarmLogRepository alarmLogRepository;

    private final UserLookupService userLookupService;

    @Transactional(readOnly = true)
    public CursorResponseDto<List<AlarmLogDto>> getAlarmLogList(Long userId, Long cursorId, int size) {
        User receiver = userLookupService.findUserById(userId);

        PageRequest pageRequest = PageRequest.of(0, size);

        // 최신순으로 조회
        Page<AlarmLog> results;
        if (cursorId == null) { // 커서가 없는 경우, 초기 페이지
            log.info("cursorId is null");
            results = alarmLogRepository.findFirstPageByReceiver(receiver, pageRequest);
        } else { // 커서가 있는 경우
            if (!alarmLogRepository.existsById(cursorId)) { // 커서가 있는 경우 존재하는지 검사
                throw new CommonException(ErrorCode.NOT_FOUND_CURSOR);
            }
            log.info("cursorId is " + cursorId);
            results = alarmLogRepository.findPageByReceiver(receiver, cursorId, pageRequest);
        }

        List<AlarmLogDto> logList = AlarmLogDto.of(results.getContent());
        CursorInfoDto pageInfo = CursorInfoDto.fromPageInfo(results);

        return CursorResponseDto.fromEntityAndPageInfo(logList, pageInfo);
    }

    @Transactional
    public List<AlarmLogDto> updateAlarmLogRead(UpdateAlarmLogDto updateAlarmLogDto) {
        List<AlarmLog> alarmLogList = alarmLogRepository.findByIdIn(updateAlarmLogDto.alarmLogIds());

        alarmLogList.forEach(AlarmLog::updateIsRead);

        alarmLogRepository.saveAll(alarmLogList);

        return AlarmLogDto.of(alarmLogList);
    }

    public Long getAlarmBadge(User user) {
        return alarmLogRepository.countByReceiverAndIsReadFalse(user);
    }

    public void createAlarmLog(CreateAlarmLogDto alarmLogDto) {
        alarmLogRepository.save(
                AlarmLog.builder()
                    .alarmType(alarmLogDto.alarmType())
                    .reply(alarmLogDto.comment())
                    .notification(alarmLogDto.notification())
                    .receiver(alarmLogDto.receiver())
                    .build()
        );
    } // 알람 로그 생성

    public void createAlarmLog(List<CreateAlarmLogDto> alarmLogDtos) {
        for (CreateAlarmLogDto alarmLogDto : alarmLogDtos) {
            createAlarmLog(alarmLogDto);
        }
    }
}
