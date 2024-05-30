package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Entities.Logs.Esignature;
import com.Guidewire.Monitoring.Entities.Logs.Rms;
import com.Guidewire.Monitoring.Entities.Logs.TransportPlugin;
import com.Guidewire.Monitoring.Repositories.EsignatureRepo;
import com.Guidewire.Monitoring.Repositories.RmsRepo;
import com.Guidewire.Monitoring.Repositories.TransportPluginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MessageQueuesService {
    @Autowired
    RmsRepo rmsRepo;

    @Autowired
    TransportPluginRepo transportPluginRepo;

    @Autowired
    EsignatureRepo esignatureRepo;
    public String getLastRmsLog(){
        Rms rms=rmsRepo.findFirstByOrderByTimestampDesc();
        if(rms!=null){
            LocalDateTime givenDateTime = LocalDateTime.parse(rms.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime currentDateTime = LocalDateTime.now();
            Duration duration = Duration.between(givenDateTime, currentDateTime);
            long days = duration.toDays();
            long hours = duration.toHours() % 24;
            long minutes = duration.toMinutes() % 60;
            return days+"/"+hours+"/"+minutes;
        }
        return null;
    }
    public String getDocProdReqLog(){
        TransportPlugin transportPlugin =transportPluginRepo.findFirstByOrderByTimestampDesc();
        if(transportPlugin!=null){
            LocalDateTime givenDateTime = LocalDateTime.parse(transportPlugin.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime currentDateTime = LocalDateTime.now();
            Duration duration = Duration.between(givenDateTime, currentDateTime);
            long days = duration.toDays();
            long hours = duration.toHours() % 24;
            long minutes = duration.toMinutes() % 60;
            return days+"/"+hours+"/"+minutes;
        }
        return null;
    }
    public String getESignatureLog(){
        Esignature esignature=esignatureRepo.findFirstByOrderByTimestampDesc();
        if(esignature!=null){
            LocalDateTime givenDateTime = LocalDateTime.parse(esignature.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime currentDateTime = LocalDateTime.now();
            Duration duration = Duration.between(givenDateTime, currentDateTime);
            long days = duration.toDays();
            long hours = duration.toHours() % 24;
            long minutes = duration.toMinutes() % 60;
            return days+"/"+hours+"/"+minutes;
        }
        return null;
    }
}
