package fr.laurentFE.todolistspringbootserver.model.exceptions;

public class DataDuplicateException extends RuntimeException {
    public  DataDuplicateException(String message) {
        super(message);
    }
}
