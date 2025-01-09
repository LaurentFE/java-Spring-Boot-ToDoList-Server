package fr.laurentFE.todolistspringbootserver.web;

import fr.laurentFE.todolistspringbootserver.model.ErrorResponse;
import fr.laurentFE.todolistspringbootserver.model.exceptions.*;
import fr.laurentFE.todolistspringbootserver.service.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(ServerService.class);

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

    @ExceptionHandler(MissingParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameterException(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Missing parameter in request body : " + e.getMessage();
        return new ResponseEntity<>(new ErrorResponse(status, message), status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

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

    @ExceptionHandler(OversizedStringProvidedException.class)
    public ResponseEntity<ErrorResponse> handleOversizedStringProvidedException(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "String parameter is too long : " + e.getMessage();
        return new ResponseEntity<>(new ErrorResponse(status, message), status);
    }

    @ExceptionHandler(OutOfSyncListIdsException.class)
    public ResponseEntity<ErrorResponse> handleOutOfSyncListIdsException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        logger.error(e.getMessage());
        String message = "Internal server error";
        return new ResponseEntity<>(new ErrorResponse(status, message), status);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        logger.debug(e.getMessage());
        String message = "Required request body is missing";
        return new ResponseEntity<>(new ErrorResponse(status, message), status);
    }
}
