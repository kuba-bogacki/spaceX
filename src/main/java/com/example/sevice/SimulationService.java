package com.example.sevice;

import com.example.model.type.MissionStatus;
import com.example.model.type.RocketStatus;

import java.util.UUID;

public interface SimulationService {
    void addNewRocket(String rocketName);
    void assignRocketToMission(UUID rocketId, UUID missionId);
    void changeRocketStatus(UUID rocketId, RocketStatus rocketStatus);
    void addNewMission(String missionName);
    void assignRocketsToTheMission(UUID missionId, UUID... rocketIds);
    void changeMissionStatus(UUID missionId, MissionStatus missionStatus);
    String getSummaryMissions();
}
