package fr.laurentFE.todolistspringbootserver.model.exceptions;

public class OutOfSyncListIdsException extends RuntimeException {
    public OutOfSyncListIdsException(String message) {
        super(message);
    }
}
