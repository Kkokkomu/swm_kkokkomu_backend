package com.kkokkomu.short_news.user.repository;

import com.kkokkomu.short_news.user.domain.User;
import com.kkokkomu.short_news.core.type.ELoginProvider;
import com.kkokkomu.short_news.core.type.EUserRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.role = :role")
    Optional<User> findByIdAndRole(Long id, EUserRole role);

    // soft 삭제된 유저 조회
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NOT NULL AND u.nickname != '탈퇴한 사용자'")
    List<User> findAllByDeletedAtIsNotNullAndIsDeleted();

    Optional<User> findByNickname(String nickname);

    Optional<User> findByEmail(String email);

    @Query("SELECT  u FROM User u order by u.id")
    List<User> findAllAscId();

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.role = :role")
    Optional<User> findByEmailAndRole(String email, EUserRole role);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.refreshToken = :refreshToken, u.isLogin = :loginStatus WHERE u.id = :id")
    void updateRefreshTokenAndLoginStatus(Long id, String refreshToken, boolean loginStatus);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.loginProvider = :loginProvider")
    Optional<User> findByIdAndELoginProvider(Long id, ELoginProvider loginProvider);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isLogin = :isLogin AND u.refreshToken IS NOT NULL")
    Optional<User> findByIdAndIsLoginAndRefreshTokenNotNull(Long id, boolean isLogin);
}
