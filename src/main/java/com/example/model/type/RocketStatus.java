package com.example.model.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RocketStatus {

    ON_GROUND("On ground"),
    IN_SPACE("In space"),
    IN_REPAIR("In repair"),;

    private final String description;
}
