package com.Guidewire.Monitoring.Repositories;

import com.Guidewire.Monitoring.Entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends JpaRepository<Account,String> {

    Page<Account> findByDocumentsPublicIDStartingWith(String center, Pageable pageable);
}
