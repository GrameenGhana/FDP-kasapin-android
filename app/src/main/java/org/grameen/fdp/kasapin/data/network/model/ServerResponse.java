package org.grameen.fdp.kasapin.data.network.model;

import java.util.List;

public class ServerResponse {
    int total_count;
    int status;
    String success;
    String message;
    List<FarmerAndAnswers> data;

    public ServerResponse() {
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public List<FarmerAndAnswers> getData() {
        return data;
    }

    public void setData(List<FarmerAndAnswers> data) {
        this.data = data;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
