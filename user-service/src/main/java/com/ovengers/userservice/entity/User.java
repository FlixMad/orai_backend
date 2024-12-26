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
    // UUID를 기본 키로 사용하는 설정
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", updatable = false, nullable = false)
    private String userId;

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

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false, name = "account_active")
    private boolean accountActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserState state; // 사용자 상태 (ex: ACTIVE, INACTIVE 등)

    @Column(nullable = false, name = "aff_id")
    private String affId; // 소속 ID

    // 연차와의 관계 (1:N)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vacation> vacations;

    // 근태와의 관계 (1:N)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attitude> attitudes;
}
