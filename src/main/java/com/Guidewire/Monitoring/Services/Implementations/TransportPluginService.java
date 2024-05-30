//Contains services to creat TransportPlugin log and extract data related to document from it

package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Entities.Logs.Log;
import com.Guidewire.Monitoring.Entities.Logs.TransportPlugin;
import com.Guidewire.Monitoring.Repositories.TransportPluginRepo;
import com.Guidewire.Monitoring.Services.Interfaces.I_TransportPlugin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class TransportPluginService implements I_TransportPlugin {
    @Autowired
    TransportPluginRepo transportPluginRepo;
    @Autowired
    LogService logService;

    public TransportPlugin createLog(Log log) throws JsonProcessingException, ParseException {
        TransportPlugin TLog= new TransportPlugin();
        TLog.setId(log.getId());
        TLog.setContent(log.getContent());
        TLog.setMessage(log.getMessage());
        TLog.setTimestamp(log.getTimestamp());
        TLog.setHost(log.getHost());
        TLog.setLevel(log.getLevel());
        TLog.setService(log.getService());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonContent = objectMapper.readTree(log.getContent());
        TLog.setStatus(logService.isRequest(jsonContent));

        if(Objects.equals(logService.getLevel(logService.getAttributes(log.getContent())), "ERROR")){
            TLog.setRequestID(getErrorRequestID(TLog));
        }else{
            TLog.setRequestID(getRequestID(TLog));
        }
        return transportPluginRepo.save(TLog);
    }


    //Related Document Data

    public Map<Integer,String> getDocumentName(TransportPlugin transportPlugin) throws JsonProcessingException {
        String attributes=logService.getAttributes(transportPlugin.getContent());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode attributesJson = objectMapper.readTree(attributes);
        JsonNode documentName=attributesJson.get("contextMap").get("documentName: ");
        Map<Integer, String> map = new HashMap<>();
        documentName.fields().forEachRemaining(entry -> {
            map.put(Integer.parseInt(entry.getKey()), entry.getValue().asText());
        });
        if (map.get(1)==null){
            map.put(1,documentName.asText());
        }
        return map;
    }
    public Map<Integer,String> getGwLinkedObject(TransportPlugin transportPlugin) throws JsonProcessingException {
        String attributes=logService.getAttributes(transportPlugin.getContent());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode attributesJson = objectMapper.readTree(attributes);
        JsonNode documentName=attributesJson.get("contextMap").get("gwLinkedObject: ");
        Map<Integer, String> map = new HashMap<>();
        documentName.fields().forEachRemaining(entry -> {
            map.put(Integer.parseInt(entry.getKey()), entry.getValue().asText());
        });
        if (map.get(1)==null){
            map.put(1,documentName.asText());
        }
        return map;
    }
    public Map<Integer,String> getDocumentTemplate(TransportPlugin transportPlugin) throws JsonProcessingException {
        String attributes=logService.getAttributes(transportPlugin.getContent());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode attributesJson = objectMapper.readTree(attributes);
        JsonNode documentName=attributesJson.get("contextMap").get("documentTemplate: ");
        Map<Integer, String> map = new HashMap<>();
        documentName.fields().forEachRemaining(entry -> {
            map.put(Integer.parseInt(entry.getKey()), entry.getValue().asText());
        });
        if (map.get(1)==null){
            map.put(1,documentName.asText());
        }
        return map;
    }
    public Map<Integer,String> getPublicID(TransportPlugin transportPlugin) throws JsonProcessingException {
        String attributes=logService.getAttributes(transportPlugin.getContent());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode attributesJson = objectMapper.readTree(attributes);
        JsonNode documentId=attributesJson.get("contextMap").get("documentId: ");
        Map<Integer, String> map = new HashMap<>();
        documentId.fields().forEachRemaining(entry -> {
            map.put(Integer.parseInt(entry.getKey()), entry.getValue().asText());
        });
        if (map.get(1)==null){
            map.put(1,documentId.asText());
        }
        return map;
    }

    @Override
    public String getRequestID(TransportPlugin transportPlugin) throws JsonProcessingException {
            String attributes=logService.getAttributes(transportPlugin.getContent());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode attributesJson = objectMapper.readTree(attributes);
            return attributesJson.get("contextMap").get("requestId: ").asText();
    }
    public String getErrorRequestID(TransportPlugin transportPlugin) throws JsonProcessingException {
        String attributes=logService.getAttributes(transportPlugin.getContent());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode attributesJson = objectMapper.readTree(attributes);
        System.out.println(attributesJson.get("exceptionMessage").asText().substring(47,47+23));
        return attributesJson.get("exceptionMessage").asText().substring(47,47+23);

    }

    public String getDeliveryMode(TransportPlugin transportPlugin) throws JsonProcessingException{
        String attributes=logService.getAttributes(transportPlugin.getContent());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode attributesJson = objectMapper.readTree(attributes);
        return attributesJson.get("contextMap").get("deliveryMode: ").toString();
    }

    public String getService(String content) throws JsonProcessingException {
        return logService.getService(content);
    }

    public String getTimeStamp(String content) throws JsonProcessingException, ParseException {
        return logService.getTimestamp(content);
    }

    public TransportPlugin findByReqID(String id){
        return transportPluginRepo.findByRequestIDAndStatus(id,true);
    }
    
}
