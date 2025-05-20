package com.vhh.PrescriptionAppBackend.model.response;

import java.util.List;

public class TimeDosageResponse {
    private String time;
    private List<DrugResponse> drugs;
    private Long id;
    private int status;
    private boolean editted;

    public boolean isEditted() {
        return editted;
    }

    public void setEditted(boolean editted) {
        this.editted = editted;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<DrugResponse> getDrugs() {
        return drugs;
    }

    public void setDrugs(List<DrugResponse> drugs) {
        this.drugs = drugs;
    }
}
