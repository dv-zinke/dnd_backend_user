package com.dnd.jachwirus.user.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RestException.class)
    public ResponseEntity<String> handleRestException(RestException restException ) {
        return ResponseEntity
                .status(restException.httpStatus)
                .header("Content-Type", "application/json")
                .body( "{ \"errorMsg\": \"" + restException.errMsg + "\"}");
    }
}
