package com.Guidewire.Monitoring.Controllers;

import com.Guidewire.Monitoring.Entities.Document;
import com.Guidewire.Monitoring.Entities.Error;
import com.Guidewire.Monitoring.Services.Implementations.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequestMapping("error")
public class ErrorController {
    @Autowired
    ErrorService errorService;
    @GetMapping("/id={id}")
    public ResponseEntity<?> getErrorByDocId(@PathVariable String id){
        return ResponseEntity.ok(errorService.getErrorByDocId((id)));
    }
    @GetMapping("/getAll")
    public ResponseEntity<?> getErrorByRead(){

        return ResponseEntity.ok(errorService.getErrors());
    }
    @PostMapping("/seen")
    public ResponseEntity<?> setSeen(@RequestParam String id){
        try {
            errorService.setSeen(id);
            return ResponseEntity.ok("Marked as seen");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}