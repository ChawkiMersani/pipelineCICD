//Contains services to extract data from Rms Response
//This class extract data from documentUpdateMetadata log

package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Entities.Document;
import com.Guidewire.Monitoring.Entities.Logs.Log;
import com.Guidewire.Monitoring.Entities.Progress;
import com.Guidewire.Monitoring.Entities.Logs.Rms;
import com.Guidewire.Monitoring.Repositories.DocumentRepo;
import com.Guidewire.Monitoring.Repositories.RmsRepo;
import com.Guidewire.Monitoring.Services.Interfaces.I_RmsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RmsService implements I_RmsService {
    @Autowired
    DocumentRepo documentRepo;
    @Autowired
    AccountService accountService;
    @Autowired
    RmsRepo rmsRepo;
    @Override
    public void updateDocumentData(Log log) throws JsonProcessingException {
        Rms rms=createLog(log);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode contentJson = objectMapper.readTree(log.getContent());
        String message= contentJson.get("message").asText();
        Boolean signed=false;

        //The Log containing document metadata verify the condition below

        if(message.startsWith("Update the document")) {
            String messageStructured = message.substring(35, message.length() - 85);

            //Transform the message payload to Json Object for standard manipulation

            String[] keyValuePairs = messageStructured.split(",");
            ObjectNode messageJson = objectMapper.createObjectNode();
            for (String pair : keyValuePairs) {
                String[] parts = pair.split("=");
                if (parts.length == 2) {
                    messageJson.put(parts[0].replaceAll("\\s", ""), parts[1]);
                }
            }

            //Update Document MetaData

            Document document =new Document();
            String publicID = messageJson.get("PublicID").toString();
            document = documentRepo.findById(publicID.substring(1, publicID.length() - 1)).get();
            document.setAuthor(messageJson.get("Author").asText());
            document.setCabinetID(messageJson.get("CabinetID_Ext").asText());
            document.setDocUID(messageJson.get("DocUID").asText());
            document.setProductionSystem(messageJson.get("ProductionSystem_Ext").asText());
            document.setDeliveryChannel(messageJson.get("DeliveryChannel_Ext").asText());
            document.setStatus(messageJson.get("Status").asText());
            document.setSecurityType(messageJson.get("SecurityType").asText());
            document.setSignatureMethod(messageJson.get("SignatureMethod_Ext").asText());
            document.setCreateTime(messageJson.get("CreateTime").asText());
            document.setUpdatetime(messageJson.get("UpdateTime").asText());
            document.setSigned(Boolean.parseBoolean(messageJson.get("Signed_Ext").asText()));
            document.setRejectionReason(messageJson.get("RejectionReason_Ext").asText());
            document.setInbound(Boolean.parseBoolean(messageJson.get("Inbound").asText()));
            document.setAccount(messageJson.get("Account").asText());
            document.setProgress(Progress.Archived);

            //Verify if the document is not correlated to other documents (Sign workflow)

            if(Objects.equals(messageJson.get("Signed_Ext").asText(), "true")){
                signed=true;
            }
            List<Log> logs=document.getLogs();
            logs.add(rms);
            document.setLogs(logs);
            documentRepo.save(document);

            //Create attached account

            accountService.createAccount(document.getAccount(), document, signed);
        }
    }

    public Rms createLog(Log log) {
        Rms rms=new Rms();
        rms.setId(log.getId());
        rms.setContent(log.getContent());
        rms.setMessage(log.getMessage());
        rms.setLevel(log.getLevel());
        rms.setHost(log.getHost());
        rms.setService(log.getService());
        rms.setTimestamp(log.getTimestamp());
        return rmsRepo.save(rms);
    }
}