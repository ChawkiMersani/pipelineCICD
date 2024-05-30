package com.Guidewire.Monitoring.Services.Interfaces;

import com.Guidewire.Monitoring.Entities.Logs.TransportPlugin;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

public interface I_TransportPlugin {
    Map<Integer,String> getDocumentName(TransportPlugin transportPlugin) throws JsonProcessingException;
    Map<Integer,String> getGwLinkedObject(TransportPlugin transportPlugin) throws JsonProcessingException;
    Map<Integer,String> getDocumentTemplate(TransportPlugin transportPlugin) throws JsonProcessingException;
    Map<Integer,String> getPublicID(TransportPlugin transportPlugin) throws JsonProcessingException;
    String getRequestID(TransportPlugin transportPlugin) throws  JsonProcessingException;

}
