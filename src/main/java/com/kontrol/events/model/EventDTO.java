package com.kontrol.events.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class EventDTO {
    public UUID eventId = UUID.randomUUID();
    @NotBlank
    public String message;
    @NotNull
    public LocalDateTime createdAt;
    @NotBlank
    public String source;

    @Override
    public String toString() {
        return "EventDTO{" +
                "eventId=" + eventId +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                ", source='" + source + '\'' +
                '}';
    }
}
