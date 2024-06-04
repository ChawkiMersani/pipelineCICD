package com.Guidewire.Monitoring.Controllers;

import com.Guidewire.Monitoring.Services.Implementations.GwLOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:51176")
@Controller
@RequestMapping("GwLinkedObject")
public class GwLinkedObjectController {
    @Autowired
    GwLOService gwLOService;
    @GetMapping("/getAll")
    ResponseEntity<?> getAllObjects(int pageNumber,int pageSize, String center){
        return ResponseEntity.ok(gwLOService.getAllGwLinkedObjects(pageNumber,pageSize,center))  ;
    }
    @GetMapping("/get")
    ResponseEntity<?> getGwLOById(@RequestParam String id){
        return ResponseEntity.ok(gwLOService.getGwLOByID(id))  ;
    }
}
