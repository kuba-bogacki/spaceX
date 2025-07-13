package com.example.model;

import com.example.model.type.RocketStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Rocket {

    private UUID id;
    private String name;
    @Setter
    private UUID missionId;
    @Setter
    private RocketStatus rocketStatus;

    public static Rocket of(String name) {
        return Rocket.builder()
                .id(UUID.randomUUID())
                .name(name)
                .rocketStatus(RocketStatus.ON_GROUND)
                .build();
    }
}
