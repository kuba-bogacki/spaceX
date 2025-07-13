package com.example.sevice.implementation;

import com.example.model.type.MissionStatus;
import com.example.repository.MissionRepository;
import com.example.repository.RocketRepository;
import com.example.repository.implementation.DefaultMissionRepository;
import com.example.repository.implementation.DefaultRocketRepository;
import com.example.sevice.MissionSimulationService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.HashMap;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultMissionSimulationService implements MissionSimulationService {

    private final MissionRepository missionRepository;
    private final RocketRepository rocketRepository;

    @Override
    public void addNewMission(String missionName) {

    }

    @Override
    public void assignRocketsToTheMission(UUID missionId, UUID... rocketIds) {

    }

    @Override
    public void changeMissionStatus(UUID missionId, MissionStatus missionStatus) {

    }

    @Override
    public String getSummaryMissions() {
        return "";
    }

    public static DefaultMissionSimulationService prod() {
        return DefaultMissionSimulationService.builder()
                .missionRepository(DefaultMissionRepository.of(new HashMap<>()))
                .rocketRepository(DefaultRocketRepository.of(new HashMap<>()))
                .build();
    }

    public static DefaultMissionSimulationService of(MissionRepository missionRepository, RocketRepository rocketRepository) {
        requireNonNull(missionRepository, "Mission repository cannot be null");
        requireNonNull(rocketRepository,  "Rocket repository cannot be null");
        return DefaultMissionSimulationService.builder()
                .missionRepository(missionRepository)
                .rocketRepository(rocketRepository)
                .build();
    }
}
