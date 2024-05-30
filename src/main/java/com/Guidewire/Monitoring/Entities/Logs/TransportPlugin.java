package com.Guidewire.Monitoring.Entities.Logs;

import jakarta.persistence.Entity;

@Entity
public class TransportPlugin extends Log {
    Boolean status=false;
    String requestID;

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public TransportPlugin(String id, String content, Boolean status) {
        super(id, content);
        this.status = status;
    }

    public TransportPlugin() {

    }
}
