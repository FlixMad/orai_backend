package com.ovengers.userservice.repository;

import com.ovengers.userservice.entity.Vacation;
import com.ovengers.userservice.entity.VacationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacationRepository extends JpaRepository<Vacation, String> {

    List<Vacation> findByVacationState(VacationState vacationState);  // PENDING 상태의 휴가를 조회
 List<Vacation>findByUserId(String userId);
}
