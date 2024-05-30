package com.Guidewire.Monitoring.Repositories;

import com.Guidewire.Monitoring.Entities.GwlinkedObject.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepo extends JpaRepository<Claim,String> {

}
