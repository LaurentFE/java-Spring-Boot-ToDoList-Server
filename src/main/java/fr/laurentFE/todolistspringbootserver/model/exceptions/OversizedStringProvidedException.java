package fr.laurentFE.todolistspringbootserver.model.exceptions;

public class OversizedStringProvidedException extends RuntimeException {
    public OversizedStringProvidedException(String message) {
        super(message);
    }
}
