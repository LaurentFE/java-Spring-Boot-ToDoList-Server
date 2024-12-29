package fr.laurentFE.todolistspringbootserver.web;

import fr.laurentFE.todolistspringbootserver.model.ErrorResponse;
import fr.laurentFE.todolistspringbootserver.model.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// This class handles custom exceptions to send the client the appropriate answer
// Unhandled Exceptions are handled by the default /error resource
@RestControllerAdvice
public class CustomExceptionController {

    @ExceptionHandler(UnexpectedParameterException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedParameterException(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Unexpected request JSON key : " + e.getMessage();
        return new ResponseEntity<>(new ErrorResponse(status, message), status);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDataNotFoundException(Exception e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = "No data was found for requested : " + e.getMessage();
        return new ResponseEntity<>(new ErrorResponse(status, message), status);
    }

    @ExceptionHandler(DataDuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDataDuplicateException(Exception e) {
        HttpStatus status = HttpStatus.CONFLICT;
        String message = "An entry already exist for requested : " + e.getMessage();
        return new ResponseEntity<>(new ErrorResponse(status, message), status);
    }

    @ExceptionHandler(IncompleteDataSetException.class)
    public ResponseEntity<ErrorResponse> handleIncompleteDataSetException(Exception e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = "Data set in database is incomplete for requested : " + e.getMessage();
        return new ResponseEntity<>(new ErrorResponse(status, message), status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Missing parameter in request body : " + e.getMessage();
        return new ResponseEntity<>(new ErrorResponse(status, message), status);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> HttpMessageNotReadableException(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Required request body is missing";
        return new ResponseEntity<>(new ErrorResponse(status, message), status);
    }
}
