package com.ovengers.etcservice.util;

import com.ovengers.etcservice.dto.NotificationEvent;
import com.ovengers.etcservice.dto.NotificationMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificationEventParser {
    public static NotificationEvent parseNotificationEvent(String jsonString) {
        // Null 또는 빈 문자열 확인
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new IllegalArgumentException("Input JSON string is null or empty");
        }

        // 문자열이 이중 따옴표로 감싸져 있다면 제거
        if (jsonString.startsWith("\"") && jsonString.endsWith("\"")) {
            jsonString = jsonString.substring(1, jsonString.length() - 1);
        }

        // 이스케이프 문자 처리
        jsonString = jsonString.replace("\\\"", "\"");

        // JSON 객체로 변환
        JSONObject jsonObject = new JSONObject(jsonString);

        // userIds 배열 파싱
        JSONArray userIdsArray = jsonObject.getJSONArray("userIds");
        List<String> userIds = new ArrayList<>();
        for (int i = 0; i < userIdsArray.length(); i++) {
            userIds.add(userIdsArray.getString(i));
        }

        // message 객체 파싱
        JSONObject messageObject = jsonObject.getJSONObject("message");

        // createdAt을 LocalDateTime으로 변환
        String createdAtString = messageObject.getString("createdAt");
        LocalDateTime createdAt = LocalDateTime.parse(createdAtString, DateTimeFormatter.ISO_DATE_TIME);

        NotificationMessage messageInfo = NotificationMessage.builder()
                .type(messageObject.getString("type"))
                .departmentId(messageObject.getString("departmentId"))
                .scheduleId(messageObject.getString("scheduleId"))
                .title(messageObject.getString("title"))
                .content(messageObject.getString("content"))
                .createdAt(createdAt) // LocalDateTime으로 설정
                .build();

        NotificationEvent notificationInfo = new NotificationEvent();
        notificationInfo.setUserIds(userIds);
        notificationInfo.setMessage(messageInfo);

        return notificationInfo;

    }
}
