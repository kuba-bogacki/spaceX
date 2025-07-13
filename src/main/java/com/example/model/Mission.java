package com.example.model;

import com.example.model.type.MissionStatus;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mission {

    private UUID id;
    private String name;
    @Setter
    private MissionStatus missionStatus;
    private List<UUID> rocketList;

    public void addRocket(UUID rocketId) {
        rocketList.add(rocketId);
    }

    public static Mission of(String name) {
        return Mission.builder()
                .id(UUID.randomUUID())
                .name(name)
                .missionStatus(MissionStatus.SCHEDULED)
                .rocketList(new ArrayList<>())
                .build();
    }
}
