package com.ovengers.userservice.repository;
import com.ovengers.userservice.entity.User;

import com.ovengers.userservice.entity.Attitude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttitudeRepository extends JpaRepository<Attitude, String> {
    Optional<Attitude> findTopByUserOrderByCreatedAtDesc(User user); // 사용자의 가장 최근 출근 기록을 조회
}

