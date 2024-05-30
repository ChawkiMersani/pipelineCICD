package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Entities.GwlinkedObject.Claim;
import com.Guidewire.Monitoring.Entities.Document;
import com.Guidewire.Monitoring.Repositories.ClaimRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClaimService {
    @Autowired
    ClaimRepo claimRepo;
    public Claim createClaim(String id, Document document){
        Optional<Claim> claim=claimRepo.findById(id);
        if(claim.isPresent()){
            Claim newClaim=claim.get();
            List<Document> docs=newClaim.getDocuments();
            if (!docs.contains(document)){
                docs.add(document);
                newClaim.setDocuments(docs);
                return claimRepo.save(newClaim);
            }
           return null;
        }else {
            Claim claim1 = new Claim();
            claim1.setId(id);
            List<Document> docs = claim1.getDocuments();
            if(!docs.contains(document)){
                docs.add(document);
                claim1.setDocuments(docs);
            }
            return claimRepo.save(claim1);
        }
    }

    public Page<Claim> getAllClaims(Pageable pageable) {
        return claimRepo.findAll(pageable);
    }

    public Claim getClaimById(String id) {
        return claimRepo.findById(id).get();
    }
}
