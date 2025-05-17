package com.fastcampus.paymentinfra.repository;

import com.fastcampus.paymentinfra.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByPhone(String phone);

    Optional<Users> findByEmail(String email);
}
