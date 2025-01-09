package fr.laurentFE.todolistspringbootserver.model.exceptions;

public class OverSizedStringProvidedException extends RuntimeException {
    public OverSizedStringProvidedException(String message) {
        super(message);
    }
}
