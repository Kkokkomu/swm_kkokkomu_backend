package com.kkokkomu.short_news.service;

import com.kkokkomu.short_news.domain.User;
import com.kkokkomu.short_news.dto.user.request.LoginDto;
import com.kkokkomu.short_news.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    public Long loginUser(LoginDto loginDto) {
        String uuid = loginDto.uuid();

        User user = userRepository.findByUuid(uuid).orElse(null);

        // 유저가 없으면 생성하고 있으면 id만 반환
        if (user == null) {
            user = User.builder()
                    .uuid(uuid)
                    .build();

            userRepository.save(user);

            return user.getId();
        } else
            return user.getId();
    }
}
