package fr.laurentFE.todolistspringbootserver.web;

import fr.laurentFE.todolistspringbootserver.model.ErrorResponse;
import fr.laurentFE.todolistspringbootserver.model.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
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

        /*
         * Doesn't make use of validation tags messages (ex: @NotEmpty(message="Field should not be empty"))
         * Crude, and should be improved upon to make use off proper DTO validation
         */
        MethodArgumentNotValidException ex = (MethodArgumentNotValidException)e;
        StringBuilder message = new StringBuilder();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()){
            message.append("Invalid value '")
                    .append(fe.getRejectedValue())
                    .append("' for field '")
                    .append(fe.getField())
                    .append("' : ")
                    .append(fe.getDefaultMessage())
                    .append(";");
        }
        return new ResponseEntity<>(new ErrorResponse(status, message.toString()), status);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Required request body is missing";
        return new ResponseEntity<>(new ErrorResponse(status, message), status);
    }
}
