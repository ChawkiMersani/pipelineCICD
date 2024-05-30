package com.Guidewire.Monitoring.Repositories;

import com.Guidewire.Monitoring.Entities.Logs.DocumentPlugin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentPluginRepo extends JpaRepository<DocumentPlugin,String> {
}
