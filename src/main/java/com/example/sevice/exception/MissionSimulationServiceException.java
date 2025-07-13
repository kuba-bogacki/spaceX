package com.example.sevice.exception;

public class MissionSimulationServiceException extends RuntimeException {

    public MissionSimulationServiceException(String message) {
        super(String.format("Mission operation could not be completed: %s", message));
    }
}
