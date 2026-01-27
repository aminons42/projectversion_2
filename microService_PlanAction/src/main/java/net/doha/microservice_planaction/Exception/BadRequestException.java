package net.doha.microservice_planaction.Exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
