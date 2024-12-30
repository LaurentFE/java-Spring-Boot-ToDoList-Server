package fr.laurentFE.todolistspringbootserver.model.exceptions;

public class IncompleteDataSetException extends RuntimeException {
    public IncompleteDataSetException(String message) {
        super(message);
    }
}
