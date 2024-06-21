package com.Guidewire.Monitoring.Controllers;

import com.Guidewire.Monitoring.Entities.Account;
import com.Guidewire.Monitoring.Services.Implementations.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:51176")
@RestController
@RequestMapping("/accountsss")

public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/getAll")
    public ResponseEntity<Page<Account>> getAllAccounts(@RequestParam(defaultValue = "0") int pageNumber,
                                                        @RequestParam(defaultValue = "100") int pageSize,
                                                        @RequestParam String center) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Account> accounts = accountService.getAllAccounts(pageable,center);
        return ResponseEntity.ok(accounts);
    }



    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable String id) {
        return accountService.getAccountById(id);
    }


}