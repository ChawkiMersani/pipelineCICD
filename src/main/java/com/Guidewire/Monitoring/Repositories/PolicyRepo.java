package com.Guidewire.Monitoring.Repositories;

import com.Guidewire.Monitoring.Entities.GwlinkedObject.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepo extends JpaRepository<Policy,String> {
}
