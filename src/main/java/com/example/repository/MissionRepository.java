package com.example.repository;

import com.example.model.Mission;
import com.example.repository.exception.MissionRepositoryException;

import java.util.List;
import java.util.UUID;

public interface MissionRepository {
    void addMission(Mission mission);
    void updateMission(UUID missionId, Mission mission) throws MissionRepositoryException;
    Mission getMissionById(UUID missionId) throws MissionRepositoryException;
    List<Mission> getAllMissions();
}
