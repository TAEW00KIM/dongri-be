package com.hufs.dongri.repository;

import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Long> {
    /**
     * 이메일로 사용자를 조회합니다. (로그인, OAuth2, UserDetailsService에서 사용)
     * @param email 사용자 이메일
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * 해당 이메일이 DB에 존재하는지 확인합니다. (회원가입 시 중복 체크)
     * @param email 사용자 이메일
     * @return boolean (존재하면 true)
     */
    boolean existsByEmail(String email);

    /**
     * 해당 학번이 DB에 존재하는지 확인합니다. (회원가입 시 중복 체크)
     * @param studentId 사용자 학번
     * @return boolean (존재하면 true)
     */
    boolean existsByStudentId(String studentId);

    List<User> findByStatus(UserStatus status);
}
