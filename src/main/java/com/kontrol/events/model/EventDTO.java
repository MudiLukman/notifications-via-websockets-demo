package com.kontrol.events.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class EventDTO {
    public UUID eventId = UUID.randomUUID();
    @NotBlank
    public String name;
    @NotNull
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime createdAt;
    @NotBlank
    public String source;

    @Override
    public String toString() {
        return "EventDTO{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", source='" + source + '\'' +
                '}';
    }
}
