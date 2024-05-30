package com.Guidewire.Monitoring.Services.Interfaces;

import com.Guidewire.Monitoring.Entities.Error;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface I_Error {
    String getStackTrace(String attributes)throws JsonProcessingException;
    Error getErrorByDocId(String id);
}
