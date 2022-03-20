package com.kontrol.websockets.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApplicantSummary {
    public long total;
    public String departmentId;

    @Override
    public String toString() {
        return "ApplicantSummary{" +
                "total=" + total +
                ", departmentId='" + departmentId + '\'' +
                '}';
    }
}
