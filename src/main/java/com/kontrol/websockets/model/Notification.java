package com.kontrol.websockets.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {
    public UUID id = UUID.randomUUID();
    public String message;
    public Object payload;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime triggeredAt;

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", triggeredAt=" + triggeredAt +
                '}';
    }
}
