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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Position position;

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false, name = "account_active")
    private boolean accountActive;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserState state;

    @JoinColumn(nullable = false, name = "aff_id")
    //소속 아이디
    private String affId;




}
