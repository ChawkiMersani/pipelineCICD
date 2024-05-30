package com.Guidewire.Monitoring.Repositories;

import com.Guidewire.Monitoring.Entities.Error;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorRepo extends JpaRepository<Error,String> {
    List<Error> findErrorsBySeen(Boolean seen);

}
