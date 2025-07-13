package com.example.sevice;

import com.example.model.type.MissionStatus;

import java.util.UUID;

public interface MissionSimulationService {
    void addNewMission(String missionName);
    void assignRocketsToTheMission(UUID missionId, UUID... rocketIds);
    void changeMissionStatus(UUID missionId, MissionStatus missionStatus);
    String getSummaryMissions();
}
