package com.example.sevice;

import com.example.model.type.RocketStatus;

import java.util.UUID;

public interface RocketSimulationService {
    void addNewRocket(String rocketName);
    void assignRocketToMission(UUID rocketId, UUID missionId);
    void changeRocketStatus(UUID rocketId, RocketStatus rocketStatus);
}
