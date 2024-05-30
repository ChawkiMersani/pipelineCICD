package com.Guidewire.Monitoring.Services.Interfaces;

import com.Guidewire.Monitoring.Entities.Logs.Log;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface I_RmsService {
    void updateDocumentData(Log log) throws JsonProcessingException;
}
