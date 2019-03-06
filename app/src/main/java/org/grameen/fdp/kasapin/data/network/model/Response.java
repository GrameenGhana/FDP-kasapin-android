package org.grameen.fdp.kasapin.data.network.model;

public class Response {

    int status;
    String message;


    public Response(){
    }


    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
