package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Entities.DTO.ErrorNotif;
import com.Guidewire.Monitoring.Entities.Document;
import com.Guidewire.Monitoring.Entities.Error;
import com.Guidewire.Monitoring.Repositories.DocumentRepo;
import com.Guidewire.Monitoring.Repositories.ErrorRepo;
import com.Guidewire.Monitoring.Services.Interfaces.I_Error;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ErrorService implements I_Error {
    @Autowired
    DocumentRepo documentRepo;
    @Autowired
    ErrorRepo errorRepo;
    public String getStackTrace(String attributes) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contentJson = objectMapper.readTree(attributes);
        return contentJson.get("stackTrace").asText();
    }

    @Override
    public Error getErrorByDocId(String id) {
        Document document=documentRepo.findById(id).get();
        return document.getError();
    }

    //Get Errors Data customized for Notification bar
    public ErrorNotif getErrors(){
        Sort sort = Sort.by(Sort.Direction.ASC, "seen");
        List<Error> errorList= errorRepo.findAll(sort);
        List<String> Ids=new ArrayList<>();
        for (Error error : errorList) {
            Document document = documentRepo.findDocumentByError(error);
            if (document != null) {
                Ids.add(document.getPublicID());
            } else {
                Ids.add("Unknown Document ID");
            }
        }
        return new ErrorNotif(errorList,Ids);
    }

    //Update the status of the notification whether seen or not

    public void setSeen(String id){
        Error error=errorRepo.findById(id).get();
        error.setSeen(true);
        errorRepo.save(error);
    }
    public String getExceptionMessage(String attributes) throws JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contentJson = objectMapper.readTree(attributes);
        return contentJson.get("exceptionMessage").asText();
    }

    //Parse the requestId from the exception Message
    public String SendDocApiErrorReqId(String exceptionMessage) {
        return exceptionMessage.substring(220,220+23);
    }

    //Get Error message for the Json object
    public String getErrorMessage(String message){
        return message.substring(39);
    }

    public String getErrorCode(String exceptionMessage){
        return exceptionMessage.substring(1,16);
    }

    //Verify whether Error is related to null object or not (For null object error there's no error message in json Object)
    public boolean IsErrorRelatedObjectNull(String message){
        return (message.endsWith("null") || message.endsWith("documents"));
    }

    // Parse the error message to get the json Object part
    public String getErrorDetails(String errorCode, String errorMessage) throws JsonProcessingException {
        String error=null;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contentJson;

        //Case 401 Unauthorized

        if(errorCode.startsWith("[401")){
            error=errorMessage.substring(errorMessage.lastIndexOf("{"));
            contentJson = objectMapper.readTree(error);
            return contentJson.get("httpMessage").asText();
        }

        //Case 400 bad request

        else if (errorCode.startsWith("[400") && errorMessage.lastIndexOf("}")==errorMessage.length()-2){
            error=errorMessage.substring(170,errorMessage.length()-1);
            contentJson = objectMapper.readTree(error);
            return contentJson.get("sendDocumentsResponse").get("body").get("status").get("errors").get(0).get("message").asText();
        }
        return errorMessage;
    }
}
