package com.Guidewire.Monitoring.Entities;

import com.Guidewire.Monitoring.Entities.Logs.Log;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity

public class Document {
    @Id
    String publicID;
    Boolean inbound;
    String name;
    String service;
    String gwLinkedObject;
    String documentTemplate;
    String deliveryChannel;
    String docUID;
    String cabinetID;
    String  author;
    String createTime;
    String productionSystem;
    String rejectionReason;
    String securityType;
    String status;
    String signatureMethod;
    Boolean signed;
    Progress progress;
    String updatetime;
    Date timestamp;
    String account;

    @JsonIgnore
    @OneToMany
    List<Log> logs= new ArrayList<>();

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @OneToOne(cascade = CascadeType.ALL)
    private Error error;
    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }




    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public Document() {
    }

    public String getPublicID() {
        return publicID;
    }

    public void setPublicID(String publicID) {
        this.publicID = publicID;
    }

    public String getDocUID() {
        return docUID;
    }

    public void setDocUID(String docUID) {
        this.docUID = docUID;
    }

    public String getCabinetID() {
        return cabinetID;
    }

    public void setCabinetID(String cabinetID) {
        this.cabinetID = cabinetID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Boolean getInbound() {
        return inbound;
    }

    public void setInbound(Boolean inbound) {
        this.inbound = inbound;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeliveryChannel() {
        return deliveryChannel;
    }

    public void setDeliveryChannel(String deliveryChannel) {
        this.deliveryChannel = deliveryChannel;
    }

    public String getDocumentTemplate() {
        return documentTemplate;
    }

    public void setDocumentTemplate(String documentTemplate) {
        this.documentTemplate = documentTemplate;
    }

    public String getProductionSystem() {
        return productionSystem;
    }

    public void setProductionSystem(String productionSystem) {
        this.productionSystem = productionSystem;
    }

    public String getGwLinkedObject() {
        return gwLinkedObject;
    }

    public void setGwLinkedObject(String gwLinkedObject) {
        this.gwLinkedObject = gwLinkedObject;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getSecurityType() {
        return securityType;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSignatureMethod() {
        return signatureMethod;
    }

    public void setSignatureMethod(String signatureMethod) {
        this.signatureMethod = signatureMethod;
    }

    public Boolean getSigned() {
        return signed;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public void setSigned(Boolean signed) {
        this.signed = signed;
    }
}
