package com.Guidewire.Monitoring.Repositories;

import com.Guidewire.Monitoring.Entities.GwlinkedObject.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepo extends JpaRepository<Submission,String>, PagingAndSortingRepository<Submission,String> {
}
