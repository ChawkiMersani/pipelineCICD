package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Entities.Logs.DocumentPlugin;
import com.Guidewire.Monitoring.Entities.Logs.Log;
import com.Guidewire.Monitoring.Repositories.DocumentPluginRepo;
import com.Guidewire.Monitoring.Services.Interfaces.I_DocumentPluginRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentPluginService implements I_DocumentPluginRepo {
    @Autowired
    DocumentPluginRepo documentPluginRepo;
    public String getDocumentName(String message){
        return message.substring(32,message.length()-1);
    }
    public DocumentPlugin createDocumentPluginLog(Log log,String attributes) throws JsonProcessingException {
        DocumentPlugin documentPlugin=new DocumentPlugin();
        documentPlugin.setId(log.getId());
        documentPlugin.setContent(log.getContent());
        documentPlugin.setMessage(log.getMessage());
        documentPlugin.setReqId(getReqId(attributes));
        return documentPluginRepo.save(documentPlugin);
    }
    public String getReqId(String attributes) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode attributesJson = objectMapper.readTree(attributes);
        return attributesJson.get("contextMap").get("requestId: ").asText();
    }

    public String getGwLinkedObject(Log newLog) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode attributesJson = objectMapper.readTree(newLog.getContent());
        return attributesJson.get("attributes").get("contextMap").get("gwLinkedObject: ").asText();
    }
}
