package com.kontrol.users.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserDTO {
    public UUID userId = UUID.randomUUID();
    @NotBlank
    public String name;
    @NotNull
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime createdAt;
    @NotNull
    public String departmentId;
    @NotNull
    public String source;

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", departmentId='" + departmentId + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
