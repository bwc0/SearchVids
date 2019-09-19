package com.searchvids.controller.ExceptionHandler;

import com.searchvids.exception.FileStorageException;
import com.searchvids.exception.ResourceNotFoundException;
import com.searchvids.model.payload.ResponseMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(Exception exception) {
        return new ResponseEntity<>(new ResponseMessage(exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase()),
                new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({FileStorageException.class})
    public ResponseEntity<?> handleFileFailureException(Exception exception) {
        return new ResponseEntity<>(new ResponseMessage(exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
