package com.ovengers.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity{
    //이거는 뭐더라 그 uuid 전략 쓰기로 하지 않았나?
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String  userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "profile_image")
    private String profileImage;

    //이게 직책 컬럼이 필요한가?, 난 잘 모르겠당 일딴 보류
//    @Column(nullable = false)
//    private Position position;
//
//    public enum Position {
//
//    }

    @Entity
    @Table(name = "tbl_vacations")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor

    public class Vacation {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long vacationId; // 연차 아이디

        @Column(nullable = false)
        private String type; // 연차 유형 (ex: 연차, 반차 등)

        @Column(nullable = false)
        private LocalDateTime startDate; // 연차 시작일

        @Column(nullable = false)
        private LocalDateTime endDate; // 연차 종료일

        @Column(nullable = false)
        private LocalDateTime createdAt; // 연차 생성일자

        private LocalDateTime updatedAt; // 연차 수정일자

        @Column(nullable = false)
        private Boolean vacationPermission; // 연차 허가 여부 (true: 허가, false: 미허가)

        @Column(nullable = false)
        private Long userId; // 사용자 아이디 (외래키)
    }
    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false, name = "account_active")
    private boolean accountActive;

    @Column(nullable = false)
    private UserState state;

    @JoinColumn(nullable = false, name = "aff_id")
    private String affId;




}
