package com.Guidewire.Monitoring.Services.Interfaces;

import com.Guidewire.Monitoring.Entities.Document;
import com.Guidewire.Monitoring.Entities.GwlinkedObject.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface I_Policy {


    Policy createPolicy(String id, Document document);


    Page<Policy> getAllPolicies(Pageable pageable);

    Policy getPolicyById(String id);
}
