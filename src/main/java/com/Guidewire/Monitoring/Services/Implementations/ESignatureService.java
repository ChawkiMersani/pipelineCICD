package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Entities.Logs.Esignature;
import com.Guidewire.Monitoring.Entities.Logs.Log;
import com.Guidewire.Monitoring.Repositories.EsignatureRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ESignatureService {
    @Autowired
    EsignatureRepo esignatureRepo;
    
    public void createLog(Log log){
        Esignature esignature= new Esignature();
        esignature.setId(log.getId());
        esignature.setContent(log.getContent());
        esignature.setMessage(log.getMessage());
        esignature.setLevel(log.getLevel());
        esignature.setHost(log.getHost());
        esignature.setService(log.getService());
        esignature.setTimestamp(log.getTimestamp());
        esignatureRepo.save(esignature);
    }
}
