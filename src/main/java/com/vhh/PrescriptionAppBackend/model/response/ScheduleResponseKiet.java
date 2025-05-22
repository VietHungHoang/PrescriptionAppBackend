package com.vhh.PrescriptionAppBackend.model.response;

import java.util.List;

public class ScheduleResponseKiet {
    private String date;
    private List<TimeDosageResponseKiet> timeDosages;
    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<TimeDosageResponseKiet> getTimeDosages() {
        return timeDosages;
    }

    public void setTimeDosages(List<TimeDosageResponseKiet> timeDosages) {
        this.timeDosages = timeDosages;
    }
}
