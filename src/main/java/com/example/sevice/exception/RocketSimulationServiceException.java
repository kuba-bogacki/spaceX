package com.example.exception;

public class RocketSimulationServiceException extends RuntimeException {

    public RocketSimulationServiceException(String message) {
        super(String.format("Rocket operation could not be completed: %s", message));
    }
}
