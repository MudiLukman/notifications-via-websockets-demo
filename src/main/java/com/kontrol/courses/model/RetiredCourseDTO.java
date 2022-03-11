package com.kontrol.courses.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.kontrol.users.model.UserDTO;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class RetiredCourseDTO {
    public int id;
    @NotNull
    public String name;
    @NotNull
    public UserDTO retiredBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime retiredAt = LocalDateTime.now();
    @NotNull
    public String source;

    @Override
    public String toString() {
        return "RetireCourseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", retiredBy=" + retiredBy +
                ", retiredAt=" + retiredAt +
                ", source='" + source + '\'' +
                '}';
    }
}
