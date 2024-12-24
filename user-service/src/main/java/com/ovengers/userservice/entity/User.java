package com.ovengers.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false, name = "account_active")
    private boolean accountActive;

    @Column(nullable = false)
    private UserState state;

    @JoinColumn(nullable = false, name = "aff_id")
    private String affId;




}
