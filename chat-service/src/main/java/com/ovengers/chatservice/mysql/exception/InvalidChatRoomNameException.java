package com.ovengers.chatservice.mysql.exception;

public class InvalidChatRoomNameException extends RuntimeException {
    public InvalidChatRoomNameException(String message) {
        super(message);
    }
}
