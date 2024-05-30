package com.Guidewire.Monitoring.Services.Implementations;

import com.Guidewire.Monitoring.Entities.Document;
import com.Guidewire.Monitoring.Entities.GwlinkedObject.Submission;
import com.Guidewire.Monitoring.Repositories.SubmissionRepo;
import com.Guidewire.Monitoring.Services.Interfaces.I_Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubmissionService implements I_Submission {
    @Autowired
    private SubmissionRepo submissionRepo;

    public Submission createSubmission(String id,Document document){
        Optional<Submission> submission=submissionRepo.findById(id);
        if(submission.isPresent()){
            List<Document> docs=submission.get().getDocuments();
            docs.add(document);
            submission.get().setDocuments(docs);
            return submissionRepo.save(submission.get());
        }else {
            Submission sub = new Submission();
            sub.setId(id);
            List<Document> docs = sub.getDocuments();
            if(!docs.contains(document)){
                docs.add(document);
                sub.setDocuments(docs);
            }
            return submissionRepo.save(sub);
        }
    }

    public Page<Submission> getSubmissions(Pageable pageable) {
        return submissionRepo.findAll(pageable);
    }


//    @Override
//    public List<Submission> getSubmissions(int pageNumber, int pageSize) {
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        return submissionRepo.findAll(pageable).getContent();
//    }


    public Submission getSubmissionById(String id) {
        Optional<Submission> optionalSubmission = submissionRepo.findById(id);
        return optionalSubmission.orElse(null);
    }



}
