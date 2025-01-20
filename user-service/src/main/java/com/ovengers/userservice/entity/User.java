package com.ovengers.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserState state;

    @Column(nullable = false, name = "department_id")
    private String departmentId;

    // User와 Attitude의 관계 (1:N)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attitude> attitudes;

    // MFA Secret Key 필드 추가
    @Column(nullable = true, name = "mfa_secret") // 필수 입력이 아닐 경우 nullable = true
    private String mfaSecret;

    // MFA Secret Key 반환 메서드 추가
    public String getUserSecret() {
        return this.mfaSecret; // mfaSecret 필드를 반환
    }

    // MFA Secret Key 설정 메서드 추가
    public void setUserSecret(String mfaSecret) {
        this.mfaSecret = mfaSecret; // mfaSecret 필드에 값을 설정
    }
}
