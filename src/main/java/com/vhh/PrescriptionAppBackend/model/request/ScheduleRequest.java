package com.vhh.PrescriptionAppBackend.model.request;

import lombok.Data;

import java.util.Date;

@Data
public class ScheduleRequest {
    private Date date;
    private Long userId;
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
