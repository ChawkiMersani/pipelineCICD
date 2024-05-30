package com.Guidewire.Monitoring.Entities.GwlinkedObject;

import com.Guidewire.Monitoring.Entities.Document;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class GwLinkedObject {
    @Id
    String id;
    @OneToMany
    List<Document> documents=new ArrayList<>();

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
