package com.Guidewire.Monitoring.Repositories;

import com.Guidewire.Monitoring.Entities.GwlinkedObject.GwLinkedObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GwLORepo extends JpaRepository<GwLinkedObject,String>, PagingAndSortingRepository<GwLinkedObject,String> {
    Page<GwLinkedObject> findByDocumentsPublicIDStartingWith(String center, Pageable pageable);
}
