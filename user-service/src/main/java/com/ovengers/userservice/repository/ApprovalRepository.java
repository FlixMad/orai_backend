package com.ovengers.userservice.repository;

import com.ovengers.userservice.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApprovalRepository extends JpaRepository<Approval, String> {

    // approvalUserId로 승인 목록을 찾는 쿼리 메서드
    List<Approval> findAllByApprovalUserId(String approvalUserId);
}
