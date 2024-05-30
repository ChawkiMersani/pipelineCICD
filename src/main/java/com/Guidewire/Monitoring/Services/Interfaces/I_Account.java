package com.Guidewire.Monitoring.Services.Interfaces;

import com.Guidewire.Monitoring.Entities.Account;
import com.Guidewire.Monitoring.Entities.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface I_Account {
    Account createAccount(String id, Document document,Boolean signed);


    //    @Override
    //    public List<Account> getAllAccounts() {
    //        return accountRepo.findAll();
    //    }
    Page<Account> getAllAccounts(Pageable pageable,String center);

    Account getAccountById(String id);
}
