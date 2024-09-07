package com.kkokkomu.short_news.core.scheduler;

import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.user.repository.UserRepository;
import com.kkokkomu.short_news.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserScheduler {
    private final UserRepository userRepository;

    private final UserService userService;

    // 자정마다 soft delete 한 지 30일이 지난 유저 삭제
    @Scheduled(cron = "0 */3 * * * *") // test 3분마다
//    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void hardDeleteUser(){
        List<User> users = userRepository.findAllByDeletedAtIsNotNullAndIsDeleted();

        List<User> usersToDelete = users.stream()
                .filter(user -> user.getDeletedAt().toLocalDate().isBefore(LocalDate.now().plusDays(1)))
                .collect(Collectors.toList());
        usersToDelete.forEach(userService::hardDeleteUser);
        log.info("deleteAllInBatch 유저 최종 삭제 완료");
    }
}