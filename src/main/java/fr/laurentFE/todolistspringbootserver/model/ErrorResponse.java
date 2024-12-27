package fr.laurentFE.todolistspringbootserver.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.util.Date;

public class ErrorResponse {

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss")
    private Date timestamp;
    private int httpCode;
    private String status;
    private String message;

    public ErrorResponse() {
        timestamp = new Date();
    }

    public ErrorResponse(HttpStatus httpStatus, String message) {
        this();
        this.httpCode = httpStatus.value();
        this.status = httpStatus.name();
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
