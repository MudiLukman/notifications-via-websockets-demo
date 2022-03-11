package com.kontrol.websockets.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {
    public UUID id = UUID.randomUUID();
    public NotificationType type;
    public String message;
    public Object payload;
    public String source;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime triggeredAt = LocalDateTime.now();

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", type=" + type +
                ", message='" + message + '\'' +
                ", payload=" + payload +
                ", source='" + source + '\'' +
                ", triggeredAt=" + triggeredAt +
                '}';
    }
}
