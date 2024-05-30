package com.Guidewire.Monitoring.Entities.DTO;

import com.Guidewire.Monitoring.Entities.Error;

import java.util.List;

public class ErrorNotif {
            private List<Error> ErrorList;
            private List<String> IDs;

    public List<Error> getErrorList() {
        return ErrorList;
    }

    public ErrorNotif(List<Error> errorList, List<String> IDs) {
        ErrorList = errorList;
        this.IDs = IDs;
    }

    public void setErrorList(List<Error> errors) {
        ErrorList = errors;
    }

    public List<String> getIDs() {
        return IDs;
    }

    public void setIDs(List<String> IDs) {
        this.IDs = IDs;
    }
}
