package com.example.repository.exception;

public class MissionRepositoryException extends Exception {

    public MissionRepositoryException(String message) {
        super(String.format("Mission database error: %s", message));
    }
}
