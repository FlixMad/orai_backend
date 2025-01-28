package com.ovengers.etcservice.common.auth;

import lombok.*;

@Setter @Getter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenUserInfo {
    private String id;           // 사용자 ID
    private String departmentId; // 부서 ID 추가
    private String Role;

    public TokenUserInfo(String id, String departmentId) {
        this.id = id;
        this.departmentId = departmentId;
    }
}

