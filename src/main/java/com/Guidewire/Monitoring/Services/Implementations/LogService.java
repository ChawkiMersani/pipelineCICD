package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Repositories.LogRepo;
import com.Guidewire.Monitoring.Services.Interfaces.I_Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class LogService implements I_Log {
    @Autowired
    LogRepo logRepo;


    public String getTimestamp(String content) throws JsonProcessingException, ParseException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contentJson = objectMapper.readTree(content);
        return contentJson.get("timestamp").asText();
    }
    public String getLevel(String attributes)throws JsonProcessingException, ParseException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contentJson = objectMapper.readTree(attributes);
        return contentJson.get("level").asText();
    }

    public String getHost(String content) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contentJson = objectMapper.readTree(content);
        return contentJson.get("host").asText();
    }
    public String getService(String content) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contentJson = objectMapper.readTree(content);
        return contentJson.get("service").asText();
    }

    public String getMessage(String content) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contentJson = objectMapper.readTree(content);
        return contentJson.get("message").asText();
    }

    public String getAttributes(String content) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contentJson = objectMapper.readTree(content);
        return contentJson.get("attributes").toString();
    }
    public String getLoggerFcn(String attributes) throws  JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contentJson = objectMapper.readTree(attributes);
        return contentJson.get("loggerName").toString();
    }
    public Boolean isRequest(JsonNode content){
        String status=content.get("message").toString().substring(39,39+7);
        return Objects.equals(status,"Request");
    }
    public Boolean isError(JsonNode content){
        String status=content.get("message").toString().substring(39,39+5);
        return Objects.equals(status,"Error");
    }
    public  String getPublicID(String content) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contentJson = objectMapper.readTree(content);
        return contentJson.get("attributes").get("contextMap").get("documentId: ").asText();
    }
}
