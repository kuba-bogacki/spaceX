package com.example.repository;

import com.example.model.Mission;

import java.util.List;
import java.util.UUID;

public interface MissionRepository {
    void addMission(Mission mission);
    void deleteMissionById(UUID missionId);
    void updateMissionById(UUID missionId, Mission mission);
    Mission getMissionById(UUID missionId);
    List<Mission> getAllMissions();
}
