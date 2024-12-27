package fr.laurentFE.todolistspringbootserver.model.exceptions;

public class DataNotFoundException  extends RuntimeException {
    public  DataNotFoundException(String message) {
        super(message);
    }
}
