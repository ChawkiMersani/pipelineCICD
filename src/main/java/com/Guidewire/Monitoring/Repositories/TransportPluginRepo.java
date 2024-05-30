package com.Guidewire.Monitoring.Repositories;

import com.Guidewire.Monitoring.Entities.Logs.TransportPlugin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportPluginRepo extends JpaRepository<TransportPlugin,String> {
    TransportPlugin findByRequestIDAndStatus(String id,Boolean bool);
    TransportPlugin findFirstByOrderByTimestampDesc();
}
