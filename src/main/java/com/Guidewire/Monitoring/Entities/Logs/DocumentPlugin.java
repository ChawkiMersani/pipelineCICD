package com.Guidewire.Monitoring.Entities.Logs;

import jakarta.persistence.Entity;

@Entity
public class DocumentPlugin extends Log {
    String reqId;

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }
}
