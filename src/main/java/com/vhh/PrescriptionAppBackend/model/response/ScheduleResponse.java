package com.vhh.PrescriptionAppBackend.model.response;

import java.util.List;

public class ScheduleResponse {
    private String date;
    private List<TimeDosageResponse> timeDosages;
    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<TimeDosageResponse> getTimeDosages() {
        return timeDosages;
    }

    public void setTimeDosages(List<TimeDosageResponse> timeDosages) {
        this.timeDosages = timeDosages;
    }
}
