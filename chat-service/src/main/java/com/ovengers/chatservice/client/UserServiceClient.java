package com.ovengers.chatservice.client;

import com.ovengers.chatservice.common.dto.CommonResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    /**
     *
     * @param userId
     * @return userResoponseDto로 정보 받아옴
     */
    @GetMapping("api/users/{userId}")
    UserResponseDto getUserById(@PathVariable("userId") String userId);

    /**
     *
     * @param params 검색 조건들, ex) ?name=charie&Position=CEO
     * @return 검색 조건에 맞는 UserResponseDto 리스트
     */
    @GetMapping("api/admin/users/list")
    CommonResDto<List<UserResponseDto>> getUsersToList(@RequestParam Map<String, String> params);
    /**
     *
     * @param params 검색 조건들
     * @param page 페이지 번호(0부터임)
     * @param size 한페이지 에 표시될 크기
     * @return
     */
    @GetMapping("api/admin/users/page")
    CommonResDto<Page<UserResponseDto>> getUsersToPage(
            @RequestParam Map<String, String> params,
            @RequestParam int page,@RequestParam int size
    );
}
