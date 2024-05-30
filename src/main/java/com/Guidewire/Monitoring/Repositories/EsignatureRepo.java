package com.Guidewire.Monitoring.Repositories;

import com.Guidewire.Monitoring.Entities.Logs.Esignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsignatureRepo extends JpaRepository<Esignature,String> {
    Esignature findFirstByOrderByTimestampDesc();
}
