package com.kontrol.websockets.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class Notification {
    public NotificationType type;
    public String message;
    public Object payload;
    public String source;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime triggeredAt = LocalDateTime.now();

    @Override
    public String toString() {
        return "Notification{" +
                "type=" + type +
                ", message='" + message + '\'' +
                ", payload=" + payload +
                ", source='" + source + '\'' +
                ", triggeredAt=" + triggeredAt +
                '}';
    }

    public enum NotificationType {
        NEW_CANDIDATE, RETIRE_COURSE
    }
}
