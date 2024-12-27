package fr.laurentFE.todolistspringbootserver.model.exceptions;

public class UnexpectedParameterException extends RuntimeException {
    public  UnexpectedParameterException(String message) {
        super(message);
    }
}