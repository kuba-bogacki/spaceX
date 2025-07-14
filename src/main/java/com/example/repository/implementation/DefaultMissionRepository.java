package com.example.repository.implementation;

import com.example.model.Mission;
import com.example.repository.MissionRepository;
import com.example.repository.exception.MissionRepositoryException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultMissionRepository implements MissionRepository {

    private static final String MISSION_ENTITY_NOT_FOUND = "Mission with id [%s] not found";

    private final Map<UUID, Mission> missionsDatabaseProvider;

    @Override
    public void addMission(Mission mission) {
        missionsDatabaseProvider.put(mission.getId(), mission);
    }

    @Override
    public void updateMission(UUID missionId, Mission mission) throws MissionRepositoryException {
        Optional.ofNullable(missionsDatabaseProvider.get(missionId))
                .map(missionEntity -> missionsDatabaseProvider.replace(missionEntity.getId(), mission))
                .orElseThrow(() -> new MissionRepositoryException(String.format(MISSION_ENTITY_NOT_FOUND, missionId)));
    }

    @Override
    public Mission getMissionById(UUID missionId) throws MissionRepositoryException {
        return Optional.ofNullable(missionsDatabaseProvider.get(missionId))
                .orElseThrow(() -> new MissionRepositoryException(String.format(MISSION_ENTITY_NOT_FOUND, missionId)));
    }

    @Override
    public List<Mission> getAllMissions() {
        return missionsDatabaseProvider.values().stream()
                .toList();
    }

    public static DefaultMissionRepository of(Map<UUID, Mission> missionsDatabaseProvider) {
        requireNonNull(missionsDatabaseProvider, "Missions database provider cannot be null");
        return new DefaultMissionRepository(missionsDatabaseProvider);
    }
}
