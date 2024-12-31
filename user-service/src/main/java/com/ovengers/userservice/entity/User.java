package com.ovengers.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Position position;

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false, name = "account_active")
    private boolean accountActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserState state;

    @JoinColumn(nullable = false, name = "aff_id") // SQL에 맞춰 이름 수정
    private String departmentId;


    // getDepartmentId 메서드 추가
    public String getDepartmentId() {
        return this.departmentId;
    }

    // Position enum 추가
    public enum Position {
        CEO,  // 추가
        MANAGER,
        TEAM_LEADER,  // 추가
        EMPLOYEE,  // 추가
        ADMIN
    }
    public enum UserState {
        IDLE,
        ACTIVE, // 필요한 상태 추가
        INACTIVE // 필요한 상태 추가
    }


}
