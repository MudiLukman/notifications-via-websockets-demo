package com.kontrol.websockets.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {
    public UUID id;
    public String action;
    public LocalDateTime triggeredAt;

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", triggeredAt=" + triggeredAt +
                '}';
    }
}
