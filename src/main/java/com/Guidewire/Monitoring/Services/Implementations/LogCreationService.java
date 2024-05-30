// This class is a median layer to process all logs and redirect them to each service needed :
// Whether TransportPlugin log /Rms log /DocumentPlugin log /Error log/ ESignature Log

package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Entities.*;
import com.Guidewire.Monitoring.Entities.Logs.DocumentPlugin;
import com.Guidewire.Monitoring.Entities.Logs.Log;
import com.Guidewire.Monitoring.Entities.Logs.TransportPlugin;
import com.Guidewire.Monitoring.Repositories.ErrorRepo;
import com.Guidewire.Monitoring.Repositories.LogRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Objects;



@Service
public class LogCreationService {
    @Autowired
    LogRepo logRepo;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    LogService logService;
    @Autowired
    DocumentService documentService;
    @Autowired
    TransportPluginService transportPluginService;
    @Autowired
    RmsService rmsService;
    @Autowired
    ErrorService errorService;
    @Autowired
    DocumentPluginService documentPluginService;
    @Autowired
    ErrorRepo errorRepo;
    @Autowired
    ESignatureService eSignatureService;

    //List of Logs :

    @Value("${DocProdRequestTransport}")
    private String DocProdRequestTransport;
    @Value("${PolicyTransportPlugin}")
    private String PolicyTransportPlugin;
    @Value("${BillingTransportPlugin}")
    private String BillingTransportPlugin;
    @Value("${ClaimTransportPlugin}")
    private String ClaimTransportPlugin;
    @Value("${PolicyRmsResponse}")
    private String PolicyRmsResponse;
    @Value("${ClaimRmsResponse}")
    private String ClaimRmsResponse;
    @Value("${BillingRmsResponse}")
    private String BillingRmsResponse;
    @Value("${PolicySendDocApi}")
    private String PolicySendDocApi;
    @Value("${BillingSendDocApi}")
    private String BillingSendDocApi;
    @Value("${ClaimSendDocApi}")
    private String ClaimSendDocApi;
    @Value("${PluginDocument}")
    private String PluginDocument;
    @Value("${PolicyDocumentUpload}")
    private String PolicyDocumentUpload;
    @Value("${ClaimDocumentUpload}")
    private String ClaimDocumentUpload;
    @Value("${ESignatureLog}")
    private String eSignature;
    @Value("${RmsDocUpdate}")
    private String RmsDocUpdate;

    public Log createLog(Object log) throws JsonProcessingException, ParseException {

        // Transform the response of the api to Json object

        JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(log));

        // Get the two Attributes of the Json object which are id and content

        String id = jsonNode.get("id").asText();

        // Transform the content to Json object

        JsonNode jsonContent=jsonNode.get("content");
        String content = jsonContent.toString();

        //Create the new log
        Log newLog= new Log();
        newLog.setId(id);
        newLog.setContent(content);
        newLog.setMessage(logService.getMessage(content));
        newLog.setHost(logService.getHost(content));
        newLog.setService(logService.getService(content));
        newLog.setTimestamp(logService.getTimestamp(content));

        //Get the log Name to redirect it to the suitable service

        String loggerFcn=logService.getLoggerFcn(logService.getAttributes(content));
        String logger = loggerFcn.substring(1,loggerFcn.length()-1);

        //(outbound) Document production log
        if(logger.equals(DocProdRequestTransport) || logger.equals(PolicyTransportPlugin) || logger.equals(ClaimTransportPlugin) ||logger.equals(BillingTransportPlugin)){
            TransportPlugin transportPlugin=transportPluginService.createLog(newLog);

            //If the log is request
            //Status: Generated

            if(logService.isRequest(jsonContent)){
                documentService.createOutboundDocument(transportPlugin);
            }

            // If the log is response and not Error
            //Status: Delivered

            else if (Objects.equals(logService.getLevel(logService.getAttributes(newLog.getContent())), "INFO")){
                documentService.setDeliver(transportPlugin);
            }


            // If the log is response and Error
            //Status: Error in delivering

            else if (Objects.equals(logService.getLevel(logService.getAttributes(newLog.getContent())), "ERROR") && logService.isError(jsonContent)){
                documentService.setDocProdError(transportPlugin,errorService.getStackTrace(logService.getAttributes(content)),logService.getMessage(content));
            }
            return transportPlugin;
        }

        //Rms response Log
        //Status: Archived

        else if(logger.equals(PolicyRmsResponse) || logger.equals(ClaimRmsResponse) || logger.equals(BillingRmsResponse)) {
            rmsService.updateDocumentData(newLog);
        }

        //sendDocAPi Log (needed in case of error to get details like error code and error message)
        //Status: Error in delivering

        else if((logger.equals(PolicySendDocApi) || logger.equals(ClaimSendDocApi) || logger.equals(BillingSendDocApi)) && Objects.equals(logService.getLevel(logService.getAttributes(newLog.getContent())), "ERROR")){
            String reqId=errorService.SendDocApiErrorReqId(errorService.getExceptionMessage(logService.getAttributes(newLog.getContent())));
            System.out.println(reqId);
            String errorMessage=errorService.getErrorMessage(newLog.getMessage());
            String errorCode=errorService.getErrorCode(errorService.getExceptionMessage(logService.getAttributes(newLog.getContent())));
            Boolean objectNull=errorService.IsErrorRelatedObjectNull(errorMessage);

            //Case null GwLinkedObject error

            if(objectNull){
                documentService.setSendDocApiError(reqId,errorMessage,errorCode,"");
            }

            //Case GwLinkedObject not null

            else{
                String details=errorService.getErrorDetails(errorCode,errorService.getExceptionMessage(logService.getAttributes(newLog.getContent())));
                documentService.setSendDocApiError(reqId,errorMessage,errorCode,details);
            }
        }

        //ESignature queue

        else if(logger.equals(eSignature) ){
            eSignatureService.createLog(newLog);
            if(newLog.getMessage().startsWith("ESignature")){
                String errorMessage=errorService.getErrorMessage(newLog.getMessage());
                String details=jsonContent.get("attributes").get("contextMap").get("ApiRejectionReason").asText();
                String stackTrace=errorService.getStackTrace(logService.getAttributes(content));
                documentService.eSignatureErrorReason(stackTrace,errorMessage,details,logService.getPublicID(newLog.getContent()));
            }
            if(Objects.equals(logService.getLevel(logService.getAttributes(newLog.getContent())), "ERROR")){
                String errorMessage=newLog.getMessage();
                String exceptionMessage=errorService.getExceptionMessage(logService.getAttributes(newLog.getContent()));
                String stackTrace=errorService.getStackTrace(logService.getAttributes(content));
                if(Objects.equals(exceptionMessage,"status code: 400")){
                    documentService.eSignatureError(stackTrace, errorMessage,"","400 Bad Request",logService.getPublicID(newLog.getContent()));
                }else{
                    documentService.eSignatureError(stackTrace, errorMessage,exceptionMessage,"",logService.getPublicID(newLog.getContent()));
                }
            }
        }
        //RmsDocUpdated queue
        else if(logger.equals(RmsDocUpdate)){
            rmsService.createLog(newLog);
            if(Objects.equals(logService.getLevel(logService.getAttributes(newLog.getContent())), "ERROR")){
                documentService.archiveError(errorService.getStackTrace(logService.getAttributes(content)),errorService.getExceptionMessage(logService.getAttributes(newLog.getContent())),errorService.getErrorMessage(newLog.getMessage()),logService.getPublicID(newLog.getContent()));
            }
        }
        //(inbound) Plugin.Document Log
        //Status: Uploaded

        else if(logger.equals(PluginDocument) && logService.getMessage(newLog.getContent()).startsWith("DocMgmt created temporary file")){
                DocumentPlugin documentPlugin= documentPluginService.createDocumentPluginLog(newLog,logService.getAttributes(newLog.getContent()));
                documentService.createInboundDocument(documentPlugin,documentPluginService.getDocumentName(newLog.getMessage()),logService.getPublicID(newLog.getContent()),logService.getService(newLog.getContent()),documentPluginService.getGwLinkedObject(newLog),logService.getTimestamp(newLog.getContent()));
        }

        //(inbound) documentUpload Log
        //Status: Sent

        else if ((logger.equals(PolicyDocumentUpload) || logger.equals(ClaimDocumentUpload))){
            if(logService.getMessage(newLog.getContent()).startsWith("HTTP Response : 200 File")){
                documentService.setProgress(logService.getPublicID(newLog.getContent()),Progress.Sent);
            }else if (logService.getMessage(newLog.getContent()).startsWith("HTTP Response :")){
                documentService.SendError(logService.getMessage(newLog.getContent()),logService.getPublicID(newLog.getContent()));
            }

        }
        return newLog;
    }
    public Log getLog(String id){
        return logRepo.findById(id).get();
    }
}
