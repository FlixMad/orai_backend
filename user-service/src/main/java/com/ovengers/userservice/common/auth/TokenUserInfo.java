package com.ovengers.userservice.common.auth;

import lombok.*;

@Setter @Getter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenUserInfo {
    private String id;       // 사용자 ID
    private String name;     // 사용자 이름
    private String email;    // 사용자 이메일 추가

    // String, String 타입의 생성자 추가
    public TokenUserInfo(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
