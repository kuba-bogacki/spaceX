package com.example.repository.implementation;

import com.example.exception.MissionRepositoryException;
import com.example.model.Mission;
import com.example.repository.MissionRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MissionRepositoryImplementation implements MissionRepository {

    private final Map<UUID, Mission> missionsDatabaseProvider;

    @Override
    public void addMission(Mission mission) {
        this.missionsDatabaseProvider.put(mission.getId(), mission);
    }

    @Override
    public void deleteMissionById(UUID missionId) {
        this.missionsDatabaseProvider.remove(missionId);
    }

    @Override
    public void updateMissionById(UUID missionId, Mission mission) {
        var missionEntity = this.missionsDatabaseProvider.get(missionId);
        if (missionEntity == null) {
            throw new MissionRepositoryException(String.format("Impossible to find mission with provided id: %s", missionId));
        }
        this.missionsDatabaseProvider.replace(missionId, mission);
    }

    @Override
    public Mission getMissionById(UUID missionId) {
        return this.missionsDatabaseProvider.get(missionId);
    }

    @Override
    public List<Mission> getAllMissions() {
        return this.missionsDatabaseProvider.values().stream()
                .toList();
    }

    public static MissionRepositoryImplementation of(Map<UUID, Mission> missionsDatabaseProvider) {
        requireNonNull(missionsDatabaseProvider, "Missions database provider cannot be null");
        return new MissionRepositoryImplementation(missionsDatabaseProvider);
    }
}
