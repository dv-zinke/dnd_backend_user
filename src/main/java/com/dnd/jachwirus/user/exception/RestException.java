package com.dnd.jachwirus.user.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = false)
@Data
public class RestException extends Exception {

    public RestException(HttpStatus httpStatus, String error) {
        this.httpStatus = httpStatus;
        this.errMsg = error;
    }
    HttpStatus httpStatus;
    String errMsg;

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
