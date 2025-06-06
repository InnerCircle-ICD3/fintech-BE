package com.fastcampus.appusermanage.repository;

import com.fastcampus.appusermanage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 사용자 찾기
    Optional<User> findByEmail(String email);

    // 이메일 중복 여부
    boolean existsByEmail(String email);
}
