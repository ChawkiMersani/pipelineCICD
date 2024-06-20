package com.Guidewire.Monitoring.Controllers;

import com.Guidewire.Monitoring.Entities.Progress;
import com.Guidewire.Monitoring.Services.Implementations.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@CrossOrigin(exposedHeaders = {"Authorization"},origins = "http://localhost:51176")
@Controller
@RequestMapping("document")

public class DocumentController {
    @Autowired
    DocumentService documentService;

    @GetMapping("/get/id={id}")
    public  ResponseEntity<?> getLog(@PathVariable String id){
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }
    @GetMapping("/getTyped/id={id}")
    public  ResponseEntity<?> getLog(@PathVariable String id,@RequestParam Boolean inbound){
        return ResponseEntity.ok(documentService.getDocumentByIdAndType(id,inbound));
    }
    @GetMapping("get/Numbers/start={start}&end={end}")
    public  ResponseEntity<?> getNumbers(@PathVariable String start,@PathVariable String end) throws ParseException {
        return ResponseEntity.ok(documentService.getNumbersByCenter(start,end));
    }
    @GetMapping("getTyped/GW_ID={id}")
    public  ResponseEntity<?> getDocumentsByGWLinkedObjectAndInboundAndType(@PathVariable String id,@RequestParam String category,@RequestParam Boolean inbound){
        return ResponseEntity.ok(documentService.getDocumentsByGWLinkedObjectAndType(id,category,inbound));
    }
    @GetMapping("getTyped/service={service}")
    public  ResponseEntity<?> getDocumentsByServiceAndType(@PathVariable String service,@RequestParam Boolean inbound,@RequestParam int pageNumber){
        return ResponseEntity.ok(documentService.getDocumentsByServiceAndType(service,inbound,pageNumber));
    }
    @GetMapping("getTyped/status={status}")
    public  ResponseEntity<?> getDocumentsByStatusAndType(@PathVariable Progress status,@RequestParam Boolean inbound,@RequestParam int pageNumber){
        return ResponseEntity.ok(documentService.getDocumentsByStatusAndType(status,inbound,pageNumber));
    }
    @GetMapping("getTyped/status={status}/service={service}")
    public ResponseEntity<?> getDocumentsByStatusAndServiceAndType(@PathVariable Progress status,@PathVariable String service,@RequestParam Boolean inbound,@RequestParam int pageNumber){
        return ResponseEntity.ok(documentService.getDocumentsByStatusAndServiceAndType(status,service,inbound,pageNumber));
    }
    @GetMapping("getTyped/status={status}/Gw_ID={id}")
    public ResponseEntity<?> getDocumentsByStatusAndGwLinkedObjectAndType(@PathVariable Progress status,@PathVariable String id,@RequestParam Boolean inbound,@RequestParam int pageNumber){
        return ResponseEntity.ok(documentService.getDocumentsByStatusAndGWLinkedObjectAndType(status,id ,inbound,pageNumber));
    }
    @GetMapping("/getAllTyped/pageNumber={pageNumber}&pageSize={pageSize}")
    public ResponseEntity<?> getDocumentsByType(@PathVariable int pageNumber, @PathVariable int pageSize,@RequestParam Boolean inbound) {
        return ResponseEntity.ok(documentService.getDocumentsAndType(pageNumber,pageSize,inbound));
    }
    @GetMapping("getTyped/service={service}/Gw_ID={id}")
    public ResponseEntity<?> getDocumentsServiceAndGwLinkedObjectAndType(@PathVariable String service,@PathVariable String id,@RequestParam Boolean inbound,@RequestParam int pageNumber){
        return ResponseEntity.ok(documentService.getDocumentsByServiceAndGwLinkedObjectAndType(service,id,inbound,pageNumber));
    }
    @GetMapping("getTyped/status={status}/service={service}/Gw_ID={id}")
    public ResponseEntity<?> getDocumentsByStatusAndServiceAndGwLinkedObjectAndType(@PathVariable Progress status,@PathVariable String service,@PathVariable String id,@RequestParam Boolean inbound,@RequestParam int pageNumber) {
        return ResponseEntity.ok(documentService.getDocumentsByStatusAndServiceAndGwLinkedObjectAndType(status, service, id,inbound,pageNumber));
    }

    @GetMapping("get/GW_ID={id}")
    public  ResponseEntity<?> getDocumentsByGWLinkedObject(@PathVariable String id,@RequestParam int pageNumber){
        return ResponseEntity.ok(documentService.getDocumentsByGWLinkedObject(id,pageNumber));
    }
    @GetMapping("get/service={service}")
    public  ResponseEntity<?> getDocumentsByService(@PathVariable String service,@RequestParam int pageNumber){
        return ResponseEntity.ok(documentService.getDocumentsByService(service,pageNumber));
    }
    @GetMapping("get/status={status}")
    public  ResponseEntity<?> getDocumentsByStatus(@PathVariable Progress status,@RequestParam int pageNumber){
        return ResponseEntity.ok(documentService.getDocumentsByStatus(status,pageNumber));
    }
    @GetMapping("get/status={status}/service={service}")
    public ResponseEntity<?> getDocumentsByStatusAndService(@PathVariable Progress status,@PathVariable String service,@RequestParam int pageNumber){
        return ResponseEntity.ok(documentService.getDocumentsByStatusAndService(status,service,pageNumber));
    }
    @GetMapping("get/status={status}/Gw_ID={id}")
    public ResponseEntity<?> getDocumentsByStatusAndGwLinkedObject(@PathVariable Progress status,@PathVariable String id,@RequestParam int pageNumber){
        return ResponseEntity.ok(documentService.getDocumentsByStatusAndGWLinkedObject(status,id,pageNumber));
    }
    @GetMapping("/getAll/pageNumber={pageNumber}&pageSize={pageSize}")
    public ResponseEntity<?> getlogs(@PathVariable int pageNumber, @PathVariable int pageSize) {
        return ResponseEntity.ok(documentService.getDocuments(pageNumber,pageSize));
    }
    @GetMapping("get/service={service}/Gw_ID={id}")
    public ResponseEntity<?> getDocuments(@PathVariable String service,@PathVariable String id,@RequestParam int pageNumber){
        return ResponseEntity.ok(documentService.getDocumentsByServiceAndGwLinkedObject(service,id,pageNumber));
    }
    @GetMapping("get/status={status}/service={service}/Gw_ID={id}")
    public ResponseEntity<?> getDocuments(@PathVariable Progress status,@PathVariable String service,@PathVariable String id,@RequestParam int pageNumber) {
        return ResponseEntity.ok(documentService.getDocumentsByStatusAndServiceAndGwLinkedObject(status, service, id,pageNumber));
    }

    @GetMapping("/documents/count-by-date")
    public Map<String, int[]> getDocumentCountsByDate(
            @RequestParam String startDate,
            @RequestParam String endDate
    ) throws ParseException {
        return documentService.getNumbersByCenter(startDate, endDate);
    }

    @GetMapping("/AccountscountsByDate/start={start}&end={end}")
    public ResponseEntity<?> getPolicyCenterDocumentCounts(@PathVariable String start, @PathVariable String end,@RequestParam String center) {
        try {
            Map<String, Integer> counts = documentService.getCenterAccountsDocumentCounts(start, end, center);
            return ResponseEntity.ok(counts);
        } catch (ParseException e) {
            // Handle parse exception (e.g., invalid date format)
            return ResponseEntity.badRequest().body("Invalid date format");
        }
    }


    @GetMapping("/PoliciescountsByDate/start={start}&end={end}")
    public ResponseEntity<?> getPolicyCenterDocumentCountsPolicies(@PathVariable String start, @PathVariable String end) {
        try {
            Map<String, Integer> counts = documentService.getPolicyCenterPoliciesDocumentCounts(start, end);
            return ResponseEntity.ok(counts);
        } catch (ParseException e) {
            // Handle parse exception (e.g., invalid date format)
            return ResponseEntity.badRequest().body("Invalid date format");
        }
    }

    @GetMapping("/SubbmissionscountsByDate/start={start}&end={end}")
    public ResponseEntity<?> getSubbmissionCenterDocumentCountsPolicies(@PathVariable String start, @PathVariable String end) {
        try {
            Map<String, Integer> counts = documentService.getPolicyCenterSubbmissionsDocumentCounts(start, end);
            return ResponseEntity.ok(counts);
        } catch (ParseException e) {
            // Handle parse exception (e.g., invalid date format)
            return ResponseEntity.badRequest().body("Invalid date format");
        }
    }
    @GetMapping("/GwLinkedObjectcountsByDate/start={start}&end={end}")
    public ResponseEntity<?> getGwLinkedObjectDocumentCountsPolicies(@PathVariable String start, @PathVariable String end) {
        try {
            Map<String, Integer> counts = documentService.getPolicyCenterGwLinkedObjectDocumentCounts(start, end);
            return ResponseEntity.ok(counts);
        } catch (ParseException e) {
            // Handle parse exception (e.g., invalid date format)
            return ResponseEntity.badRequest().body("Invalid date format");
        }
    }

// Endpoint to get document counts by service and type within a date range
    @GetMapping("/countByServiceAndType")
    public ResponseEntity<?> getDocumentCountsByServiceAndType(
            @RequestParam String service,
            @RequestParam Boolean inbound,
            @RequestParam String start,
            @RequestParam String end) {
        try {
            Map<String, Integer> counts = documentService.countDocumentsByServiceAndType(service, inbound, start, end);
            return ResponseEntity.ok(counts);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format: " + e.getMessage());
        }
    }
    @GetMapping("/DocumentsByWeekorMonth")
    public ResponseEntity<?> getDocumentCountByWeekorMonth(@RequestParam Boolean isWeek){
        return ResponseEntity.ok(documentService.getDocumentsByWeekorMonth(isWeek)) ;
    }

    @GetMapping("/CentersNumbers")
    public  ResponseEntity<?> getCentersNumbers(@RequestParam String start,@RequestParam String end) throws ParseException {
        return ResponseEntity.ok(documentService.getCentersNumbers(start,end));
    }
    @GetMapping("/ErrorsNumbers")
    public  ResponseEntity<?> getErrorsNumbers(@RequestParam String start,@RequestParam String end) throws ParseException {
        return ResponseEntity.ok(documentService.getErrorsNumbers(start,end));
    }
    @GetMapping("/DocumentsErrors")
    public  ResponseEntity<?> getDocumentsErrors(@RequestParam String start,@RequestParam String end,@RequestParam String center) throws ParseException {
        return ResponseEntity.ok(documentService.getDocumentsErrors(start,end,center));
    }
    @GetMapping("/countByServiceTypeAndDate")
    public ResponseEntity<?> getDocumentCountsByServiceTypeAndDate(
            @RequestParam String service,
            @RequestParam Boolean inbound,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            System.out.println("Hiii");
            Long counts = documentService.getDocumentCountsByServiceTypeAndDate(service, inbound, startDate, endDate);
            System.out.println(counts);
            return ResponseEntity.ok(counts);


        } catch (ParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format: " + e.getMessage());
        }
    }
}

