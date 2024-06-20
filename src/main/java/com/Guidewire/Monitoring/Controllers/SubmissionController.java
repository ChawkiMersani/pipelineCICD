package com.Guidewire.Monitoring.Controllers;


import com.Guidewire.Monitoring.Entities.GwlinkedObject.Submission;
import com.Guidewire.Monitoring.Services.Implementations.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:51176")
@RestController
@RequestMapping("/submissions")

public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @GetMapping("/getAll")
    public ResponseEntity<Page<Submission>> getSubmissions(@RequestParam(defaultValue = "0") int pageNumber,
                                                           @RequestParam(defaultValue = "100") int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Submission> submissions = submissionService.getSubmissions(pageable);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/{id}")
    public Submission getSubmissionById(@PathVariable String id) {
        return submissionService.getSubmissionById(id);
    }
}
