package com.Guidewire.Monitoring.Services.Interfaces;

import com.Guidewire.Monitoring.Entities.GwlinkedObject.GwLinkedObject;
import org.springframework.data.domain.Page;

public interface I_GwLinkedObject {
    Page<GwLinkedObject> getAllGwLinkedObjects(int pageNumber, int pagSize,String center);
    GwLinkedObject getGwLOByID(String id);
}
