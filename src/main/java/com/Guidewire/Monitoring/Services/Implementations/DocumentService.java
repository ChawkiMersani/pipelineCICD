package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Entities.*;
import com.Guidewire.Monitoring.Entities.Error;
import com.Guidewire.Monitoring.Entities.Logs.DocumentPlugin;
import com.Guidewire.Monitoring.Entities.Logs.Log;
import com.Guidewire.Monitoring.Entities.Logs.TransportPlugin;
import com.Guidewire.Monitoring.Repositories.DocumentRepo;
import com.Guidewire.Monitoring.Repositories.ErrorRepo;
import com.Guidewire.Monitoring.Services.Interfaces.I_Document;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.print.Doc;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DocumentService implements I_Document {
    @Autowired
    DocumentRepo documentRepo;
    @Autowired
    TransportPluginService transportPluginService;
    @Autowired
    PolicyService policyService;
    @Autowired
    SubmissionService submissionService;
    @Autowired
    AccountService accountService;
    @Autowired
    ErrorRepo errorRepo;
    @Autowired
    ClaimService claimService;

    // Service to create OutBout Document based on transportPlugin Log
    // Services to create GwLinkedObject are Called inside
    public void createOutboundDocument(TransportPlugin transportPlugin) throws JsonProcessingException {
        transportPluginService.getPublicID(transportPlugin).forEach((key, value) -> {
            Document document=new Document();
            document.setPublicID(value);
            try {
                document.setName(transportPluginService.getDocumentName(transportPlugin).get(key));
                document.setDocumentTemplate(transportPluginService.getDocumentTemplate(transportPlugin).get(key));
                String linkedObjectID=transportPluginService.getGwLinkedObject(transportPlugin).get(key);
                document.setGwLinkedObject(linkedObjectID);
                String deliverymode=transportPluginService.getDeliveryMode(transportPlugin);
                document.setDeliveryChannel(deliverymode.substring(1,deliverymode.length()-1));
                document.setService(transportPluginService.getService(transportPlugin.getContent()));
                LocalDateTime localDateTime = LocalDateTime.parse(transportPluginService.getTimeStamp(transportPlugin.getContent()), DateTimeFormatter.ISO_DATE_TIME);
                Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                document.setTimestamp(date);
                document.setInbound(false);
                document.setProgress(Progress.Generated);
                List<Log> logs=new ArrayList<>();
                logs.add(transportPlugin);
                document.setLogs(logs);
                documentRepo.save(document);


                switch (document.getService()){
                    case "PolicyCenter" :

                        //Check if GwLinkedObject is Policy to create policy object

                        if(linkedObjectID.startsWith("BE")){
                            policyService.createPolicy(linkedObjectID,document);
                        }

                        //Check if GwLinkedObject is Submission to create submission object

                        else if (linkedObjectID.startsWith("000")) {
                            submissionService.createSubmission(linkedObjectID,document);
                        }
                        break;
                    case "ClaimCenter" :
                        claimService.createClaim(linkedObjectID,document);
                        break;
                    case "BillingCenter" :
                        accountService.createAccount(linkedObjectID,document,false);
                }

            } catch (JsonProcessingException | ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // get All dcuments by page
    @Override
    public List<Document> getDocuments(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return documentRepo.findAllByOrderByTimestampDesc(pageable).getContent();
    }
    // get numbers of documents by period in each phase (consumed in pieChart in documents interface in the front)
    @Override
    public Map<String, int[]> getNumbersByCenter(String start, String end) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
         Date s=dateFormat.parse(start);
         Date e=dateFormat.parse(end);
        List<Document> documents=documentRepo.findByTimestampBetween(s,e);
        Map<String,int[]> map= new HashMap<>();
        int[] sum={0,0,0,0,0};
        int[] lp = {0,0,0,0,0};
        int[] lb = {0,0,0,0,0};
        int[] lc = {0,0,0,0,0};
        int[] err={0,0,0,0,0};
        for(Document doc : documents){
            switch (doc.getService()) {
                case "PolicyCenter":
                    numbersGenerator(lp,sum, err,0, doc);
                    break;
                case "BillingCenter":
                    numbersGenerator(lb,sum, err,1, doc);
                    break;
                case "ClaimCenter":
                    numbersGenerator(lc,sum, err,2, doc);
                    break;
            }
        }
        map.put("policy",lp);
        map.put("claim",lc);
        map.put("billing",lb);
        map.put("sum",sum);
        map.put("error",err);
        return map;
    }
    private void numbersGenerator(int[] lp, int[] sum,int[] err,int i, Document doc) {
        switch (doc.getProgress()){
            case Generated:
                lp[0]++;
                sum[i]++;
                break;
            case Archived:
                lp[2]++;
                sum[i]++;
                break;
            case Transmitted:
                lp[1]++;
                sum[i]++;
                break;
            case Uploaded:
                lp[3]++;
                sum[i]++;
                break;
            case Sent:
                lp[4]++;
                sum[i]++;
                break;
            case  ErrorArchiving, ErrorGenerating, ErrorDelivering, ErrorSigning, ErrorSending:
                sum[i]++;
                err[i]++;
                break;

        }
    }
    // Filters for all documents
    @Override
    public List<Document> getDocumentsByGWLinkedObject(String id, int pageNumber) {
        Pageable pageable=PageRequest.of(pageNumber,10);
        return documentRepo.findDocumentsByGwLinkedObjectOrderByTimestampDesc(id,pageable).getContent();
    }

    @Override
    public List<Document> getDocumentsByService(String service,int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        return documentRepo.findDocumentsByServiceOrderByTimestampDesc(service,pageable).getContent();
    }

    @Override
    public List<Document> getDocumentsByStatus(Progress status,int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        if(status==Progress.Error ){
            List<Progress> statuss=new ArrayList<>();
            statuss.add(Progress.ErrorDelivering);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorGenerating);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorSigning);
            statuss.add(Progress.ErrorSending);
            return documentRepo.findDocumentsByProgressInOrderByTimestampDesc(statuss,pageable).getContent();
        }
        return documentRepo.findDocumentsByProgressOrderByTimestampDesc(status,pageable).getContent();
    }

    @Override
    public List<Document> getDocumentsByStatusAndService(Progress status,String service,int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        if(status==Progress.Error ){
            List<Progress> statuss=new ArrayList<>();
            statuss.add(Progress.ErrorDelivering);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorGenerating);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorSigning);
            statuss.add(Progress.ErrorSending);
            return documentRepo.findDocumentsByProgressInAndServiceOrderByTimestampDesc(statuss,service,pageable).getContent();
        }
        return documentRepo.findDocumentsByProgressAndServiceOrderByTimestampDesc(status,service,pageable).getContent();
    }

    @Override
    public List<Document> getDocumentsByStatusAndGWLinkedObject(Progress status,String id,int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        if(status==Progress.Error ){
            List<Progress> statuss=new ArrayList<>();
            statuss.add(Progress.ErrorDelivering);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorGenerating);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorSigning);
            statuss.add(Progress.ErrorSending);
            return documentRepo.findDocumentsByProgressInAndGwLinkedObjectOrderByTimestampDesc(statuss,id,pageable).getContent();
        }
        return documentRepo.findDocumentsByProgressAndGwLinkedObjectOrderByTimestampDesc(status,id,pageable).getContent();
    }

    @Override
    public List<Document> getDocumentsByServiceAndGwLinkedObject(String service, String id,int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        return documentRepo.findDocumentsByServiceAndGwLinkedObjectOrderByTimestampDesc(service,id,pageable).getContent();
    }

    @Override
    public List<Document> getDocumentsByStatusAndServiceAndGwLinkedObject(Progress status,String service,String id,int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        if(status==Progress.Error ){
            List<Progress> statuss=new ArrayList<>();
            statuss.add(Progress.ErrorDelivering);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorGenerating);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorSigning);
            statuss.add(Progress.ErrorSending);
            return documentRepo.findDocumentsByProgressInAndServiceAndGwLinkedObjectOrderByTimestampDesc(statuss,service,id,pageable).getContent();
        }
        return documentRepo.findDocumentsByProgressAndServiceAndGwLinkedObjectOrderByTimestampDesc(status,service,id,pageable).getContent();
    }
    @Override
    public Document getDocumentById(String id) {
        return documentRepo.findById(id).get();
    }

    //Filters for Documents by type (inbound/outbound)

    public Object getDocumentsAndType(int pageNumber, int pageSize, Boolean inbound) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return documentRepo.findDocumentsByInboundOrderByTimestampDesc(pageable,inbound).getContent();
    }
    @Override
    public List<Document> getDocumentsByGWLinkedObjectAndType(String id,String category, Boolean inbound) {
        switch (category){
            case "policy":
                List<Document> policyDocs=policyService.getPolicyById(id).getDocuments();
                List<Document> policyDocsTyped=new ArrayList<>();
                for(Document doc : policyDocs ){
                    if(doc.getInbound()== inbound){
                        policyDocsTyped.add(doc);
                    }
                }
                return policyDocsTyped;
            case "claim":
                List<Document> claimDocs=claimService.getClaimById(id).getDocuments();
                List<Document> claimDocsTyped=new ArrayList<>();
                for(Document doc : claimDocs ){
                    if(doc.getInbound()== inbound){
                        claimDocs.add(doc);
                    }
                }
                return claimDocs;
            case "billing":
                List<Document> billingDocs=accountService.getAccountById(id).getDocuments();
                List<Document> billingDocsTyped=new ArrayList<>();
                for(Document doc : billingDocs ){
                    if(doc.getInbound()== inbound){
                        billingDocsTyped.add(doc);
                    }
                }
                return billingDocsTyped;
            case "Account":
                List<Document> accountDocs=accountService.getAccountById(id).getDocuments();
                List<Document> accountDocsTyped=new ArrayList<>();
                for(Document doc : accountDocs ){
                    if(doc.getInbound()== inbound){
                        accountDocsTyped.add(doc);
                    }
                }
                return accountDocsTyped;
            case "Submission":
                List<Document> submissionDocs=submissionService.getSubmissionById(id).getDocuments();
                List<Document> submissionDocsTyped=new ArrayList<>();
                for(Document doc : submissionDocs ){
                    if(doc.getInbound()== inbound){
                        submissionDocsTyped.add(doc);
                    }
                }
                return submissionDocsTyped;
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public List<Document> getDocumentsByServiceAndType(String service, Boolean inbound,int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        return documentRepo.findDocumentsByServiceAndInboundOrderByTimestampDesc(service,inbound,pageable).getContent();
    }

    @Override
    public List<Document> getDocumentsByStatusAndType(Progress status, Boolean inbound,int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        if(status==Progress.Error ){
            List<Progress> statuss=new ArrayList<>();
            statuss.add(Progress.ErrorDelivering);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorGenerating);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorSigning);
            statuss.add(Progress.ErrorSending);
            return documentRepo.findDocumentsByProgressInAndInboundOrderByTimestampDesc(statuss,inbound,pageable).getContent();
        }
        return documentRepo.findDocumentsByProgressAndInboundOrderByTimestampDesc(status,inbound,pageable).getContent();
    }

    @Override
    public List<Document> getDocumentsByStatusAndServiceAndType(Progress status, String service, Boolean inbound,int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        if(status==Progress.Error ){
            List<Progress> statuss=new ArrayList<>();
            statuss.add(Progress.ErrorDelivering);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorGenerating);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorSigning);
            statuss.add(Progress.ErrorSending);
            return documentRepo.findDocumentsByProgressInAndServiceAndInboundOrderByTimestampDesc(statuss,service,inbound,pageable).getContent();
        }
        return documentRepo.findDocumentsByProgressAndServiceAndInboundOrderByTimestampDesc(status,service,inbound,pageable).getContent();
    }

    @Override
    public List<Document> getDocumentsByStatusAndGWLinkedObjectAndType(Progress status, String id, Boolean inbound,int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        if(status==Progress.Error ){
            List<Progress> statuss=new ArrayList<>();
            statuss.add(Progress.ErrorDelivering);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorGenerating);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorSigning);
            statuss.add(Progress.ErrorSending);
            return documentRepo.findDocumentsByProgressInAndGwLinkedObjectAndInboundOrderByTimestampDesc(statuss,id,inbound,pageable).getContent();
        }
        return documentRepo.findDocumentsByProgressAndGwLinkedObjectAndInboundOrderByTimestampDesc(status,id,inbound,pageable).getContent();
    }

    @Override
    public List<Document> getDocumentsByServiceAndGwLinkedObjectAndType(String service, String id, Boolean inbound,int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        return documentRepo.findDocumentsByServiceAndGwLinkedObjectAndInboundOrderByTimestampDesc(service,id,inbound,pageable).getContent();
    }

    @Override
    public List<Document> getDocumentsByStatusAndServiceAndGwLinkedObjectAndType(Progress status, String service, String id, Boolean inbound,int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        if(status==Progress.Error ){
            List<Progress> statuss=new ArrayList<>();
            statuss.add(Progress.ErrorDelivering);
            statuss.add(Progress.ErrorArchiving);
            statuss.add(Progress.ErrorGenerating);
            statuss.add(Progress.ErrorSigning);
            statuss.add(Progress.ErrorSending);
            return documentRepo.findDocumentsByProgressInAndServiceAndGwLinkedObjectAndInboundOrderByTimestampDesc(statuss,service,id,inbound,pageable).getContent();
        }
        return documentRepo.findDocumentsByProgressAndServiceAndGwLinkedObjectAndInboundOrderByTimestampDesc(status,service,id,inbound,pageable).getContent();
    }

    @Override
    public Document getDocumentByIdAndType(String id, Boolean inbound) {
        return documentRepo.findDocumentByPublicIDAndInbound(id,inbound);
    }

    @Override
    public void setDeliver(TransportPlugin transportPlugin) throws JsonProcessingException {
        TransportPlugin request=transportPluginService.findByReqID(transportPluginService.getRequestID(transportPlugin));
        for(String id :transportPluginService.getPublicID(request).values()){
            Document document=documentRepo.findById(id).get();
            document.setProgress(Progress.Transmitted);
            documentRepo.save(document);
        };
    }

    //Create error for DocumentProduction log

    public void setDocProdError(TransportPlugin transportPlugin, String stack,String message) throws JsonProcessingException {
        TransportPlugin request=transportPluginService.findByReqID(transportPluginService.getErrorRequestID(transportPlugin));
        System.out.println(transportPluginService.getErrorRequestID(transportPlugin));
        for(String id :transportPluginService.getPublicID(request).values()){
            Document document=documentRepo.findById(id).get();
            document.setProgress(Progress.ErrorDelivering);
            Error error= document.getError();
            if(error==null){
                error= new Error();
            }
            error.setStackTrace(stack);
            error.setMessage(message);
            errorRepo.save(error);
            document.setError(error);
            documentRepo.save(document);
        };
    }

    //Create error for SendDocApi log

    public void setSendDocApiError(String reqID,String errorMessage,String errorCode,String details) throws JsonProcessingException {
        TransportPlugin request=transportPluginService.findByReqID(reqID);
        for(String id :transportPluginService.getPublicID(request).values()){
            Error error=new Error();
            error.setErrorMessage(errorMessage);
            error.setErrorCode(errorCode);
            if(!details.isEmpty()){
                error.setDetails(details);
            }
            errorRepo.save(error);
            Document document=documentRepo.findById(id).get();
            document.setProgress(Progress.ErrorDelivering);
            document.setError(error);
            documentRepo.save(document);
        };
    }

    @Override
    public Map<String, Integer> getCenterAccountsDocumentCounts(String start, String end,String center) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormat.parse(start);
        Date endDate = dateFormat.parse(end);

        Map<String, Integer> dailyCounts = new LinkedHashMap<>();

        // Initialize map with all dates between start and end, inclusive, with counts set to 0

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        while (!startCal.after(endCal)) {
            String currentKey = keyFormat.format(startCal.getTime());
            dailyCounts.put(currentKey, 0);
            startCal.add(Calendar.DATE, 1);
        }

        List<Document> documents = documentRepo.findByTimestampBetween(startDate, endDate);

        for (Document doc : documents) {
            switch (center){
                case"policy":
                    if ("PolicyCenter".equals(doc.getService()) && doc.getAccount()!=null) {
                        String docDateKey = keyFormat.format(doc.getTimestamp());

                        dailyCounts.put(docDateKey, dailyCounts.getOrDefault(docDateKey, 0) + 1);
                    }
                    break;
                case "claim":
                    if ("ClaimCenter".equals(doc.getService()) && doc.getAccount()!=null) {
                        String docDateKey = keyFormat.format(doc.getTimestamp());

                        dailyCounts.put(docDateKey, dailyCounts.getOrDefault(docDateKey, 0) + 1);
                    }
                    break;
                case "billing":
                    if ("BillingCenter".equals(doc.getService()) && doc.getAccount()!=null) {
                        String docDateKey = keyFormat.format(doc.getTimestamp());

                        dailyCounts.put(docDateKey, dailyCounts.getOrDefault(docDateKey, 0) + 1);
                    }
                    break;
            }
            if ("PolicyCenter".equals(doc.getService()) && doc.getAccount()!=null) {
                String docDateKey = keyFormat.format(doc.getTimestamp());

                dailyCounts.put(docDateKey, dailyCounts.getOrDefault(docDateKey, 0) + 1);
            }
        }

        return dailyCounts;
    }

    @Override
    public Map<String, Integer> getPolicyCenterPoliciesDocumentCounts(String start, String end) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormat.parse(start);
        Date endDate = dateFormat.parse(end);

        Map<String, Integer> dailyCounts = new LinkedHashMap<>();

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        while (!startCal.after(endCal)) {
            String currentKey = keyFormat.format(startCal.getTime());
            dailyCounts.put(currentKey, 0);
            startCal.add(Calendar.DATE, 1);
        }

        List<Document> documents = documentRepo.findByTimestampBetween(startDate, endDate);

        for (Document doc : documents) {
            if ("PolicyCenter".equals(doc.getService()) && doc.getGwLinkedObject().startsWith("BE")) {
                String docDateKey = keyFormat.format(doc.getTimestamp());
                dailyCounts.put(docDateKey, dailyCounts.getOrDefault(docDateKey, 0) + 1);
            }
        }

        return dailyCounts;
    }

    @Override
    public Map<String, Integer> getPolicyCenterSubbmissionsDocumentCounts(String start, String end) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormat.parse(start);
        Date endDate = dateFormat.parse(end);

        Map<String, Integer> dailyCounts = new LinkedHashMap<>();

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        while (!startCal.after(endCal)) {
            String currentKey = keyFormat.format(startCal.getTime());
            dailyCounts.put(currentKey, 0);
            startCal.add(Calendar.DATE, 1);
        }

        List<Document> documents = documentRepo.findByTimestampBetween(startDate, endDate);

        for (Document doc : documents) {
            if ("PolicyCenter".equals(doc.getService()) && doc.getGwLinkedObject().startsWith("000")) {
                String docDateKey = keyFormat.format(doc.getTimestamp());
                dailyCounts.put(docDateKey, dailyCounts.getOrDefault(docDateKey, 0) + 1);
            }
        }

        return dailyCounts;
    }

    @Override
    public Map<String, Integer> getPolicyCenterGwLinkedObjectDocumentCounts(String start, String end) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormat.parse(start);
        Date endDate = dateFormat.parse(end);

        Map<String, Integer> dailyCounts = new LinkedHashMap<>();

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        while (!startCal.after(endCal)) {
            String currentKey = keyFormat.format(startCal.getTime());
            dailyCounts.put(currentKey, 0);
            startCal.add(Calendar.DATE, 1);
        }

        List<Document> documents = documentRepo.findByTimestampBetween(startDate, endDate);

        for (Document doc : documents) {
                String docDateKey = keyFormat.format(doc.getTimestamp());
                dailyCounts.put(docDateKey, dailyCounts.getOrDefault(docDateKey, 0) + 1);
        }

        return dailyCounts;
    }

    // Service to create OutBout Document based on PluginDocument or uploadDocument Logs
    // Services to create GwLinkedObject are Called inside

    public void createInboundDocument(DocumentPlugin log, String documentName, String id, String service, String GwLo, String timeStamp) {
        Document document=new Document();
        document.setName(documentName);
        document.setPublicID(id);
        document.setInbound(true);
        document.setProgress(Progress.Uploaded);
        document.setService(service);
        document.setGwLinkedObject(GwLo);
        List<Log> logs=new ArrayList<>();
        logs.add(log);
        document.setLogs(logs);
        LocalDateTime localDateTime = LocalDateTime.parse(timeStamp, DateTimeFormatter.ISO_DATE_TIME);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        document.setTimestamp(date);
        documentRepo.save(document);

        //Check if GwLinkedObject is Policy to create policy object

        if(GwLo.startsWith("BE")){
            policyService.createPolicy(GwLo,document);
        }

        //Check if GwLinkedObject is Submission to create submission object

        else if (GwLo.startsWith("000")) {
            submissionService.createSubmission(GwLo,document);
        }

    }

    //Update the phase of the document

    public void setProgress(String id,Progress progress) {
        Optional<Document> document=documentRepo.findById(id);
        if(document.isPresent()){
            Document newDocument=document.get();
            newDocument.setProgress(progress);
            documentRepo.save(newDocument);
        }else{
            throw new ObjectNotFoundException(Optional.of(id),"Document");
        }
    }
    public Object getClaimsCount(String start, String end) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormat.parse(start);
        Date endDate = dateFormat.parse(end);

        Map<String, Integer> dailyCounts = new LinkedHashMap<>();

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        while (!startCal.after(endCal)) {
            String currentKey = keyFormat.format(startCal.getTime());
            dailyCounts.put(currentKey, 0);
            startCal.add(Calendar.DATE, 1);
        }

        List<Document> documents = documentRepo.findByTimestampBetween(startDate, endDate);

        for (Document doc : documents) {
            if(Objects.equals(doc.getService(), "ClaimCenter")){
            String docDateKey = keyFormat.format(doc.getTimestamp());
            dailyCounts.put(docDateKey, dailyCounts.getOrDefault(docDateKey, 0) + 1);
        }
        }
        return dailyCounts;
    }
    public void eSignatureError(String stackTrace, String errorMessage, String exceptionMessage, String errorCode, String id) {
        Optional<Document> document=documentRepo.findById(id);
        if(document.isPresent()){
            Document newDocument=document.get();
            Error error= newDocument.getError();
            if(error==null){
                error=new Error();
            }
            error.setDetails(exceptionMessage);
            error.setMessage(errorMessage);
            error.setErrorCode(errorCode);
            error.setDetails(exceptionMessage);
            error.setStackTrace(stackTrace);
            errorRepo.save(error);
            newDocument.setProgress(Progress.ErrorSigning);
            newDocument.setError(error);
            documentRepo.save(newDocument);
        }
    }
    public void eSignatureErrorReason(String stackTrace,String errorMessage,String details, String id) {
        Optional<Document> document = documentRepo.findById(id);
        if (document.isPresent()) {
            Document newDocument = document.get();
            Error error = newDocument.getError();
            if (error == null) {
                error = new Error();
            }
            error.setDetails(details);
            error.setMessage(errorMessage);
            error.setStackTrace(stackTrace);
            errorRepo.save(error);
            newDocument.setProgress(Progress.ErrorSigning);
            newDocument.setError(error);
            documentRepo.save(newDocument);
        }
    }
    public void archiveError(String stackTrace, String exceptionMessage,String errorMessage, String id) {
        Optional<Document> document = documentRepo.findById(id);
        if (document.isPresent()) {
            Document newDocument = document.get();
            Error error = newDocument.getError();
            if (error == null) {
                error = new Error();
            }
            error.setErrorCode(exceptionMessage.substring(19));
            error.setMessage(errorMessage);
            error.setStackTrace(stackTrace);
            errorRepo.save(error);
            newDocument.setProgress(Progress.ErrorArchiving);
            newDocument.setError(error);
            documentRepo.save(newDocument);
        }
    }

    public void SendError(String message, String publicID) {
        String errorCode=message.substring(16);
        Optional<Document> document=documentRepo.findById(publicID);
        if(document.isPresent()){
            Document newDocument=document.get();
            Error error= newDocument.getError();
            if(error==null){
                error=new Error();
            }
            error.setErrorCode(errorCode);
            errorRepo.save(error);
            newDocument.setProgress(Progress.ErrorSending);
            newDocument.setError(error);
            documentRepo.save(newDocument);
        }
    }
    public List<Object> getDocumentsByWeekorMonth(Boolean isWeek){
        List<Object> response=new ArrayList<>();

        LocalDateTime today=LocalDateTime.now();
        if(isWeek){
            LocalDateTime weekBegining=today.minusDays(6);
            LocalDateTime day=weekBegining;
            for (;!day.isAfter(today);day=day.plusDays(1)){

                LocalTime endOfDay = LocalTime.of(23, 59, 59);
                LocalDateTime start=day.toLocalDate().atStartOfDay();
                LocalDateTime end=LocalDateTime.of(day.toLocalDate(),endOfDay);
                Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
                Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
                List<Document> documents =documentRepo.findByTimestampBetween(startDate, endDate);
                int policyNumber=0;
                int claimNumber=0;
                int billingNumber=0;
                for(Document document : documents){
                    switch (document.getService()){
                        case "PolicyCenter":
                            policyNumber++;
                            break;
                        case "ClaimCenter":
                            claimNumber++;
                            break;
                        case "BillingCenter":
                            billingNumber++;
                            break;
                    }
                }
                Map<String,Object> object=new HashMap<>();
                object.put("name",day.getDayOfWeek().name().substring(0,3));
                object.put("Policy",policyNumber);
                object.put("Claim",claimNumber);
                object.put("Billing",billingNumber);
                response.add(object);
            }
        }else {
            LocalDateTime firstDayOfMonth = LocalDateTime.of(today.getYear(),today.getMonth(), 1, 0, 0, 0);
            for (LocalDateTime day = firstDayOfMonth; !day.isAfter(today);day = day.plusDays(1)) {
                LocalTime endOfDay = LocalTime.of(23, 59, 59);
                LocalDateTime start = day.toLocalDate().atStartOfDay();
                LocalDateTime end = LocalDateTime.of(day.toLocalDate(), endOfDay);
                Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
                Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
                List<Document> documents = documentRepo.findByTimestampBetween(startDate, endDate);
                int policyNumber = 0;
                int claimNumber = 0;
                int billingNumber = 0;
                for (Document document : documents) {
                    switch (document.getService()) {
                        case "PolicyCenter":
                            policyNumber++;
                            break;
                        case "ClaimCenter":
                            claimNumber++;
                            break;
                        case "BillingCenter":
                            billingNumber++;
                            break;
                    }
                }
                Map<String,Object> object=new HashMap<>();
                object.put("name", day.getDayOfMonth());
                object.put("Policy", policyNumber);
                object.put("Claim", claimNumber);
                object.put("Billing", billingNumber);
                response.add(object);

            }
        }
        return response;
    }

    public List<Integer> getCentersNumbers(String start, String end) throws ParseException {
        List<Integer> response=new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        Date s=dateFormat.parse(start);
        Date e=dateFormat.parse(end);
        List<Document> documents=documentRepo.findByTimestampBetween(s,e);
        int policy=0,claim=0,billing=0;
        for(Document document : documents){
            switch (document.getService()){
                case "PolicyCenter":
                    policy++;
                    break;
                case "ClaimCenter":
                    claim++;
                    break;
                case "BillingCenter":
                    billing++;
                    break;
            }
        }
        response.add(policy);
        response.add(claim);
        response.add(billing);
        return response;
    }

    public List<List<Integer>> getErrorsNumbers(String start, String end) throws ParseException {
        List<List<Integer>> response=new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        Date s=dateFormat.parse(start);
        Date e=dateFormat.parse(end);
        List<Progress> statuss=new ArrayList<>();
        statuss.add(Progress.ErrorDelivering);
        statuss.add(Progress.ErrorArchiving);
        statuss.add(Progress.ErrorGenerating);
        statuss.add(Progress.ErrorArchiving);
        statuss.add(Progress.ErrorSigning);
        statuss.add(Progress.ErrorSending);
        List<Document> documents=documentRepo.findByProgressInAndTimestampBetween(statuss,s,e);
        Integer[] policy={0,0,0,0,0,0},claim={0,0,0,0,0,0},billing={0,0,0,0,0,0};
        for(Document document : documents){
            switch (document.getProgress()){
                case ErrorDelivering:
                    switch (document.getService()){
                        case "PolicyCenter":
                            policy[0]++;
                            break;
                        case "ClaimCenter":
                            claim[0]++;
                            break;
                        case "BillingCenter":
                            billing[0]++;
                            break;
                    }
                    break;
                case ErrorArchiving:
                    switch (document.getService()){
                        case "PolicyCenter":
                            policy[2]++;
                            break;
                        case "ClaimCenter":
                            claim[2]++;
                            break;
                        case "BillingCenter":
                            billing[2]++;
                            break;
                    }
                    break;
                case ErrorSigning:
                    switch (document.getService()){
                        case "PolicyCenter":
                            policy[4]++;
                            break;
                        case "ClaimCenter":
                            claim[4]++;
                            break;
                        case "BillingCenter":
                            billing[4]++;
                            break;
                    }
                    break;
                case ErrorSending:
                    switch (document.getService()){
                        case "PolicyCenter":
                            policy[5]++;
                            break;
                        case "ClaimCenter":
                            claim[5]++;
                            break;
                        case "BillingCenter":
                            billing[5]++;
                            break;
                    }
                    break;
            }
        }
        response.add(Arrays.asList(policy));
        response.add(Arrays.asList(claim));
        response.add(Arrays.asList(billing));
        return response;
    }

    public List<Integer> getDocumentsErrors(String start, String end,String center) throws ParseException {
        Map<String, int[]> numbers=getNumbersByCenter(start,end);
        List<Integer> response=new ArrayList<>();
        switch (center){
            case "Policy":
                response.add(numbers.get("sum")[0]);
                response.add(numbers.get("error")[0]);
                break;
            case "Claim":
                response.add(numbers.get("sum")[1]);
                response.add(numbers.get("error")[1]);
                break;
            case "Billing":
                response.add(numbers.get("sum")[2]);
                response.add(numbers.get("error")[2]);
                break;
        }
        return response;
    }
    // Method to count documents by service and type (inbound or outbound) within a specific date range
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Map<String, Integer> countDocumentsByServiceAndType(String service, Boolean inbound, String startDateStr, String endDateStr) throws ParseException {
        Date startDate = dateFormat.parse(startDateStr);
        Date endDate = dateFormat.parse(endDateStr);
        List<Document> documents = documentRepo.findDocumentsByServiceAndInboundAndTimestampBetween(service, inbound, startDate, endDate);
        Map<String, Integer> counts = new HashMap<>();
        counts.put("Total", documents.size());
        return counts;
    }
    public Long getDocumentCountsByServiceTypeAndDate(String service, Boolean inbound, String startDateStr, String endDateStr) throws ParseException {
        Date startDate = dateFormat.parse(startDateStr);
        Date endDate = dateFormat.parse(endDateStr);
        List<Document> documents = documentRepo.findDocumentsByServiceAndInboundAndTimestampBetween(service, inbound, startDate, endDate);

        return documents.stream().count() ;
    }
}
