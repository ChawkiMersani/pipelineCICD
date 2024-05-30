package com.Guidewire.Monitoring.Services.Interfaces;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.text.ParseException;
import java.time.LocalDateTime;

public interface I_Log {
    public String getTimestamp(String content) throws JsonProcessingException, ParseException;
    String getHost(String content) throws JsonProcessingException;
    String getService(String content) throws JsonProcessingException;
    String getMessage(String content) throws JsonProcessingException;
    String getAttributes(String content) throws JsonProcessingException;
    String getLoggerFcn(String attributes) throws  JsonProcessingException;
    Boolean isRequest(JsonNode content);

}
