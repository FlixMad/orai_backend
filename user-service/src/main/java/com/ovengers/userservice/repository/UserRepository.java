package com.ovengers.userservice.repository;

import com.ovengers.userservice.entity.Position;
import com.ovengers.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {  // ID 타입을 String으로 설정
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);  // ID를 기준으로 조회하는 메서드
    Optional<User> findByMfaSecret(String mfaSecret);  // secret으로 사용자 조회하는 메서드 추가
    List<User> findByUserIdIn(List<String> userIds);  // ID를 기준으로 다중 조회하는 메서드

    Optional<User> findByPositionAndDepartmentId(Position position, String departmentId);


    // 새로 추가된 메서드
    boolean existsByEmail(String email);
}
