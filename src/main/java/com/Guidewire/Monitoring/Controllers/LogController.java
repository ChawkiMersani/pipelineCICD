package com.Guidewire.Monitoring.Controllers;

import com.Guidewire.Monitoring.Entities.Logs.Log;
import com.Guidewire.Monitoring.Services.Implementations.LogCreationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@CrossOrigin(origins = "http://localhost:62344")
@Controller
@RequestMapping("log")
public class LogController {
    @Autowired
    LogCreationService logCreationService;

    @PostMapping("/add")
    public ResponseEntity<?> addLog(@RequestBody Object log) {
        try {
            return ResponseEntity.ok(logCreationService.createLog(log));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format");
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing log");
        }
    }

    @GetMapping("/get/id={id}")
    public ResponseEntity<?> getLog(@PathVariable String id) {
        Log log = logCreationService.getLog(id);
        if (log == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Log not found");
        }
        return ResponseEntity.ok(log);
    }
}