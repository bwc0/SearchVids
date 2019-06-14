package com.searchvids.model.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchvids.model.User;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@JsonInclude(Include.NON_NULL)
public class ResponseMessage {

    private String message;
    private String status;
    private User user;

    public ResponseMessage(String message, String status, User user) {
        this.message = message;
        this.status = status;
        this.user = user;
    }

    public ResponseMessage(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
