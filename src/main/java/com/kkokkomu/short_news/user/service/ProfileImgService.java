package com.kkokkomu.short_news.user.service;

import com.kkokkomu.short_news.core.config.service.S3Service;
import com.kkokkomu.short_news.user.domain.ProfileImg;
import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.user.repository.ProfileImgRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static com.kkokkomu.short_news.core.constant.Constant.DEFAULT_PROFILE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImgService {
    private final ProfileImgRepository profileImgRepository;

    private final S3Service s3Service;

    public ProfileImg findProfileImgByUser(User user) {
        return profileImgRepository.findByUser(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_PROFILE_IMG));
    } // 유저로부터 프로필 이미지 조회

    public ProfileImg putProfileImg(MultipartFile img, User user) {
        ProfileImg profileImg = profileImgRepository.findByUser(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_PROFILE_IMG));

        // 새로운 프사 s3 업로드
        String profileImgUrl = s3Service.putUserProfile(img, user.getId());

        profileImg.updateImg(profileImgUrl);

        return profileImgRepository.save(profileImg);
    } // 새 프사 업로드

    public ProfileImg putProfileImgDefault(User user) {
        ProfileImg profileImg = profileImgRepository.findByUser(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_PROFILE_IMG));

        // 기존 프사 삭제
        s3Service.deleteUserProfileByUrl(profileImg.getImgUrl());

        // 기본 이미지로 변경
        profileImg.updateImg(DEFAULT_PROFILE);

        return profileImgRepository.save(profileImg);
    } // 기본 프사로 변경

    public void toDefaultProfileImg(User user) {
        ProfileImg profileImg = findProfileImgByUser(user);

        if (!Objects.equals(profileImg.getImgUrl(), DEFAULT_PROFILE)) {
            profileImg.putDefaultImg();
        }
    } // 프로필 이미지 디폴트 이미지로 변경
}
