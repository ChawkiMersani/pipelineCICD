package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Entities.Account;
import com.Guidewire.Monitoring.Entities.Document;
import com.Guidewire.Monitoring.Repositories.AccountRepo;
import com.Guidewire.Monitoring.Repositories.DocumentRepo;
import com.Guidewire.Monitoring.Services.Interfaces.I_Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements I_Account {
    @Autowired
    AccountRepo accountRepo;
    @Autowired
    DocumentRepo documentRepo;

    @Override
    public Account createAccount(String id, Document document,Boolean signed) {
        Optional<Account> account=accountRepo.findById(id);
        if(account.isPresent()){
            List<Document> docs=account.get().getDocuments();
            if(signed){
                for(Document doc: docs){
                    if(document.getName().startsWith(doc.getName().substring(0,doc.getName().length()-2))){
                        doc.setSigned(true);
                        documentRepo.save(doc);
                    }
                }
            }
            if (!docs.contains(document)){
                docs.add(document);
            }
            Account acc = account.get();
            acc.setDocuments(docs);
            return accountRepo.save(acc);
        }else {
            Account acc = new Account();
            acc.setId(id);
            List<Document> docs = acc.getDocuments();
            if(!docs.contains(document)){
                docs.add(document);
                acc.setDocuments(docs);
            }
            return accountRepo.save(acc);
        }
    }

    //    @Override
//    public List<Account> getAllAccounts() {
//        return accountRepo.findAll();
//    }
    @Override
    public Page<Account> getAllAccounts(Pageable pageable, String center) {
        return switch (center) {
            case "billing" -> accountRepo.findByDocumentsPublicIDStartingWith("bc", pageable);
            case "policy" -> accountRepo.findByDocumentsPublicIDStartingWith("pc", pageable);
            case "claim" -> accountRepo.findByDocumentsPublicIDStartingWith("cc", pageable);
            default -> null;
        };
    }

    @Override
    public Account getAccountById(String id) {
        Optional<Account> optionalAccount = accountRepo.findById(id);
        return optionalAccount.orElse(null);
    }
}
