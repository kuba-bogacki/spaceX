package com.example.exception;

public class RocketRepositoryException extends RuntimeException {

    public RocketRepositoryException(String message) {
        super(String.format("Rocket database error: %s", message));
    }
}
