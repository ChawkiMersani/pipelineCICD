package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Entities.GwlinkedObject.GwLinkedObject;
import com.Guidewire.Monitoring.Repositories.GwLORepo;
import com.Guidewire.Monitoring.Services.Interfaces.I_GwLinkedObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GwLOService implements I_GwLinkedObject {
    @Autowired
    GwLORepo gwLORepo;
    @Override
    public Page<GwLinkedObject> getAllGwLinkedObjects(int pageNumber, int pageSize,String center) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return switch (center) {
            case "billing" -> gwLORepo.findByDocumentsPublicIDStartingWith("bc", pageable);
            case "policy" -> gwLORepo.findByDocumentsPublicIDStartingWith("pc", pageable);
            case "claim" -> gwLORepo.findByDocumentsPublicIDStartingWith("cc", pageable);
            default -> null;
        };
    }

    @Override
    public GwLinkedObject getGwLOByID(String id) {
        return gwLORepo.findById(id).get();
    }
}
