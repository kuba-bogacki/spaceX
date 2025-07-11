package com.example.model;

import com.example.model.type.RocketStatus;
import lombok.*;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Rocket {

    private UUID id;
    private String name;
    private UUID missionId;
    private RocketStatus rocketStatus;

    public static Rocket of(String name) {
        return Rocket.builder()
                .id(UUID.randomUUID())
                .name(name)
                .rocketStatus(RocketStatus.ON_GROUND)
                .build();
    }

//    public void sendToMission(UUID missionId) {
//        this.missionId = missionId;
//        this.rocketStatus = RocketStatus.IN_SPACE;
//    }
//
//    public void repairRocket() {
//        this.rocketStatus = RocketStatus.IN_REPAIR;
//    }
//
//    public boolean isRepairing() {
//        return this.rocketStatus == RocketStatus.IN_REPAIR;
//    }
}
