package com.ovengers.userservice.entity;

public enum VacationState {
  PENDING("진행중"),
    APPROVED("승인됨"),
    REJECTED("거절됨");

    private final String description;

    VacationState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
