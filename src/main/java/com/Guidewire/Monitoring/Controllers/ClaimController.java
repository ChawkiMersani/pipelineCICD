package com.Guidewire.Monitoring.Controllers;

import com.Guidewire.Monitoring.Entities.GwlinkedObject.Claim;
import com.Guidewire.Monitoring.Services.Implementations.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin
@Controller
@RequestMapping("claim")
public class ClaimController {
    @Autowired
    ClaimService claimService;
    @GetMapping("/getAll")
    public ResponseEntity<Page<Claim>> getAllAccounts(@RequestParam(defaultValue = "0") int pageNumber,
                                                      @RequestParam(defaultValue = "100") int pageSize){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Claim> claims = claimService.getAllClaims(pageable);
        return ResponseEntity.ok(claims);
    }
}
