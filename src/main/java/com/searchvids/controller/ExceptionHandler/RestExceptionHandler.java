package com.searchvids.controller.ExceptionHandler;

import com.searchvids.exception.ResourceNotFoundException;
import com.searchvids.model.payload.ResponseMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(Exception exception) {
        return new ResponseEntity<>(new ResponseMessage(exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase()),
                new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
}
