package com.hufs.dongri.repository;

import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByStudentId(String studentId);
    List<User> findByStatus(UserStatus status);
}
