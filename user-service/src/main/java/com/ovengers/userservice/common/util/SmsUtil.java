package com.ovengers.userservice.common.util;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import net.nurigo.sdk.message.model.Message;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsUtil {

//    @Value("${coolsms.api.key}")
//    private String apiKey;
//    @Value("${coolsms.api.secret}")
//    private String apiSecretKey;

    private DefaultMessageService messageService;

//    @PostConstruct
//    private void init(){
//        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, "https://api.coolsms.co.kr");
//    }

    public SingleMessageSentResponse sendOne(String to) {
        Message message = new Message();
        // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
        message.setFrom("01063338645");
        message.setTo(to);
        message.setText("[Orai] 사내 메신저 서비스 오라이 입니다. 다음 링크로 접속해주세요\n" + "www.naver.com");
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        return response;
    }

}
