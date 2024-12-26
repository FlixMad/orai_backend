package com.ovengers.userservice.common.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class TokenUserInfo {
    private String id;       // 사용자 ID
//    private String affId;    // 소속 ID
    private String name;     // 사용자 이름
}
