package com.example.model.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MissionStatus {

    SCHEDULED("Scheduled"),
    PENDING("Pending"),
    IN_PROGRESS("In progress"),
    ENDED("Ended");

    private final String description;
}
