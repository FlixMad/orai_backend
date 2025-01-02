package com.ovengers.userservice.repository;

import com.ovengers.userservice.entity.Attitude;
import com.ovengers.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttitudeRepository extends JpaRepository<Attitude, String> {

    List<Attitude> findByUser(User user);
}
