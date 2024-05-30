package com.Guidewire.Monitoring.Repositories;

import com.Guidewire.Monitoring.Entities.Document;
import com.Guidewire.Monitoring.Entities.Error;
import com.Guidewire.Monitoring.Entities.Progress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DocumentRepo extends JpaRepository<Document,String>, PagingAndSortingRepository<Document,String> {
    List<Document> findByTimestampBetween(Date start, Date end);
    Page<Document> findDocumentsByGwLinkedObjectOrderByTimestampDesc(String id,Pageable pageable);
    Page<Document> findDocumentsByServiceOrderByTimestampDesc(String service,Pageable pageable);
    Page<Document> findDocumentsByProgressOrderByTimestampDesc(Progress progress,Pageable pageable);
    Page<Document> findDocumentsByProgressAndGwLinkedObjectOrderByTimestampDesc(Progress progress,String id,Pageable pageable);
    Page<Document> findDocumentsByProgressAndServiceOrderByTimestampDesc(Progress progress,String service,Pageable pageable);
    Page<Document> findDocumentsByServiceAndGwLinkedObjectOrderByTimestampDesc(String service,String id,Pageable pageable);

    Page<Document> findDocumentsByProgressAndServiceAndGwLinkedObjectOrderByTimestampDesc(Progress progress,String service,String id,Pageable pageable);

    Page<Document> findDocumentsByProgressInAndServiceAndGwLinkedObjectOrderByTimestampDesc(List<Progress> progress,String service,String id,Pageable pageable);
    Page<Document> findDocumentsByProgressInAndGwLinkedObjectOrderByTimestampDesc(List<Progress> progress, String id,Pageable pageable);
    Page<Document> findDocumentsByProgressInAndServiceOrderByTimestampDesc(List<Progress> progress, String service,Pageable pageable);
    Page<Document> findDocumentsByProgressInOrderByTimestampDesc(List<Progress> progress,Pageable pageable);
    Document findDocumentByError(Error error);
    Page<Document> findDocumentsByGwLinkedObjectAndInboundOrderByTimestampDesc(String id,Boolean inbound,Pageable pageable);
    Page<Document> findDocumentsByServiceAndInboundOrderByTimestampDesc(String service,Boolean inbound,Pageable pageable);
    Page<Document> findDocumentsByProgressAndInboundOrderByTimestampDesc(Progress progress,Boolean inbound,Pageable pageable);
    Page<Document> findDocumentsByProgressAndGwLinkedObjectAndInboundOrderByTimestampDesc(Progress progress,String id,Boolean inbound,Pageable pageable);
    Page<Document> findDocumentsByProgressAndServiceAndInboundOrderByTimestampDesc(Progress progress,String service,Boolean inbound,Pageable pageable);
    Page<Document> findDocumentsByServiceAndGwLinkedObjectAndInboundOrderByTimestampDesc(String service,String id,Boolean inbound,Pageable pageable);

    Page<Document> findDocumentsByProgressAndServiceAndGwLinkedObjectAndInboundOrderByTimestampDesc(Progress progress,String service,String id,Boolean inbound,Pageable pageable);
    Document findDocumentByPublicIDAndInbound(String id, Boolean inbound);

    Page<Document> findDocumentsByProgressInAndServiceAndGwLinkedObjectAndInboundOrderByTimestampDesc(List<Progress> progress,String service,String id,Boolean inbound,Pageable pageable);
    Page<Document> findDocumentsByProgressInAndGwLinkedObjectAndInboundOrderByTimestampDesc(List<Progress> progress, String id,Boolean inbound,Pageable pageable);
    Page<Document> findDocumentsByProgressInAndServiceAndInboundOrderByTimestampDesc(List<Progress> progress, String service,Boolean inbound,Pageable pageable);
    Page<Document> findDocumentsByProgressInAndInboundOrderByTimestampDesc(List<Progress> progress,Boolean inbound,Pageable pageable);

    Page<Document> findAllByOrderByTimestampDesc(Pageable pageable);
    Page<Document> findDocumentsByInboundOrderByTimestampDesc(Pageable pageable, Boolean inbound);

    List<Document> findByProgressInAndTimestampBetween(List<Progress> statuss, Date s, Date e);

    @Query("SELECT d FROM Document d WHERE d.service = :service AND d.inbound = :inbound AND d.timestamp BETWEEN :startDate AND :endDate")
    List<Document> findDocumentsByServiceAndInboundAndTimestampBetween(String service, Boolean inbound, Date startDate, Date endDate);

    @Query("SELECT d FROM Document d WHERE d.service = :service AND d.timestamp BETWEEN :startDate AND :endDate")
    List<Document> findDocumentsByServiceAndTimestampBetween(String service, Date startDate, Date endDate);
}
