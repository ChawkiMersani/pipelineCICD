package com.Guidewire.Monitoring.Controllers;

import com.Guidewire.Monitoring.Services.Implementations.MessageQueuesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(origins = "http://localhost:62344")
@Controller
@RequestMapping("messagesqueues")
public class MessageQueuesController {
    @Autowired
    private MessageQueuesService messageQueuesService;

    @GetMapping("/getRmsStatus")
    public ResponseEntity<String> getRmsStatus() {
        return ResponseEntity.ok(messageQueuesService.getLastRmsLog());
    }

    @GetMapping("/getDocProdStatus")
    public ResponseEntity<String> getDocProdReqStatus() {
        return ResponseEntity.ok(messageQueuesService.getDocProdReqLog());
    }
    @GetMapping("/getEsignatureStatus")
    public ResponseEntity<String> getEsignatureStatus() {
        return ResponseEntity.ok(messageQueuesService.getESignatureLog());
    }
}
