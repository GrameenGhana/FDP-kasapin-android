package org.grameen.fdp.kasapin.data.network.model;

import java.util.List;

public class SyncDownData {

    String success;
    int total_count;

    List<FarmerAndAnswers> data;



    public SyncDownData (){}


    public void setData(List<FarmerAndAnswers> data) {
        this.data = data;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getTotal_count() {
        return total_count;
    }

    public List<FarmerAndAnswers> getData() {
        return data;
    }

    public String getSuccess() {
        return success;
    }
}
