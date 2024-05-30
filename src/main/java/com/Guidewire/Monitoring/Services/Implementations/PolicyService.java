package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Entities.Document;
import com.Guidewire.Monitoring.Entities.GwlinkedObject.Policy;
import com.Guidewire.Monitoring.Repositories.PolicyRepo;
import com.Guidewire.Monitoring.Services.Interfaces.I_Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyService implements I_Policy {
    @Autowired
    PolicyRepo policyRepo;


    @Override
    public Policy createPolicy(String id,Document document){
        Optional<Policy> policy=policyRepo.findById(id);
        if(policy.isPresent()){
            List<Document> docs=policy.get().getDocuments();
            if (!docs.contains(document)){
                docs.add(document);
            }
            docs.add(document);
            policy.get().setDocuments(docs);
            return policyRepo.save(policy.get());
        }else {
            Policy pol = new Policy();
            pol.setId(id);
            List<Document> docs = pol.getDocuments();
            if(!docs.contains(document)){
                docs.add(document);
                pol.setDocuments(docs);
            }
            return policyRepo.save(pol);
        }
    }

    @Override
    public Page<Policy> getAllPolicies(Pageable pageable) {
        return policyRepo.findAll(pageable);
    }

    @Override
    public Policy getPolicyById(String id) {
        Optional<Policy> optionalPolicy = policyRepo.findById(id);
        return optionalPolicy.orElse(null);
    }







}
