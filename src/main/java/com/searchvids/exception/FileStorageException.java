package com.searchvids.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileStorageException extends RuntimeException {

    public FileStorageException() {
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
