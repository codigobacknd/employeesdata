package com.creh.employees.exception;

import com.creh.employees.response.Response;
import com.creh.employees.response.ResponseDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<Response> handleFileProcessingException(FileProcessingException ex) {
        Response errorResponse = new Response(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null,
                Instant.now().toString(),
                new ResponseDetail("FileProcessingException", "The provided file is invalid or corrupted")
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<Response> handleDatabaseOperationException(DatabaseOperationException ex) {
        Response errorResponse = new Response(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                null,
                Instant.now().toString(),
                new ResponseDetail("DatabaseOperationException", "Error saving employee or roles to database")
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleGenericException(Exception ex) {
        Response errorResponse = new Response(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error occurred",
                null,
                Instant.now().toString(),
                new ResponseDetail("Exception", ex.getMessage())
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
