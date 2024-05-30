package com.Guidewire.Monitoring.Services.Interfaces;

import com.Guidewire.Monitoring.Entities.Document;
import com.Guidewire.Monitoring.Entities.Progress;
import com.Guidewire.Monitoring.Entities.Logs.TransportPlugin;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface I_Document {
    void createOutboundDocument(TransportPlugin transportPlugin) throws JsonProcessingException;
    List<Document> getDocuments(int pageNumber, int pageSize);

    Map<String, int[]> getNumbersByCenter(String start, String end) throws ParseException;

    List<Document> getDocumentsByGWLinkedObject(String id,int pageNumber);
    List<Document> getDocumentsByService(String Service, int pageNumber);
    List<Document> getDocumentsByStatus(Progress status, int pageNumber);
    List<Document> getDocumentsByStatusAndService(Progress status,String service, int pageNumber);
    List<Document> getDocumentsByStatusAndGWLinkedObject(Progress status,String id, int pageNumber);
    List<Document> getDocumentsByServiceAndGwLinkedObject(String service, String id, int pageNumber);
    List<Document> getDocumentsByStatusAndServiceAndGwLinkedObject(Progress status,String service,String id, int pageNumber);
    Document getDocumentById(String id);
    void setDeliver(TransportPlugin transportPlugin) throws JsonProcessingException;

    Map<String, Integer> getCenterAccountsDocumentCounts(String start, String end,String center) throws ParseException;

    Map<String, Integer> getPolicyCenterPoliciesDocumentCounts(String start, String end) throws ParseException;

    Map<String, Integer> getPolicyCenterSubbmissionsDocumentCounts(String start, String end) throws ParseException;
    Map<String, Integer> getPolicyCenterGwLinkedObjectDocumentCounts(String start, String end) throws ParseException;
    List<Document> getDocumentsByGWLinkedObjectAndType(String id,String category, Boolean inbound);
    List<Document> getDocumentsByServiceAndType(String Service,Boolean inbound, int pageNumber);
    List<Document> getDocumentsByStatusAndType(Progress status,Boolean inbound, int pageNumber);
    List<Document> getDocumentsByStatusAndServiceAndType(Progress status,String service,Boolean inbound, int pageNumber);
    List<Document> getDocumentsByStatusAndGWLinkedObjectAndType(Progress status,String id,Boolean inbound, int pageNumber);
    List<Document> getDocumentsByServiceAndGwLinkedObjectAndType(String service, String id,Boolean inbound, int pageNumber);
    List<Document> getDocumentsByStatusAndServiceAndGwLinkedObjectAndType(Progress status,String service,String id,Boolean inbound, int pageNumber);
    Document getDocumentByIdAndType(String id,Boolean inbound);
}
