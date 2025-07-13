package com.example.sevice.implementation;

import com.example.repository.exception.MissionRepositoryException;
import com.example.model.Mission;
import com.example.repository.MissionRepository;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Getter
class FakeMissionRepository extends SamplesRepository implements MissionRepository {

    private final Map<UUID, Mission> sampleMissionRepository = new HashMap<>();
    private Mission spyMission;
    private boolean missionUpdated;

    @Override
    public void addMission(Mission mission) {
        this.spyMission = mission;
        sampleMissionRepository.put(missionNo1.getId(), mission);
    }

    @Override
    public void updateMission(UUID missionId, Mission mission) {
        this.spyMission = mission;
        this.missionUpdated = true;
        sampleMissionRepository.replace(missionId, mission);
    }

    @Override
    public Mission getMissionById(UUID missionId) throws MissionRepositoryException {
        return sampleMissionRepository.get(missionId);
    }

    @Override
    public List<Mission> getAllMissions() {
        return sampleMissionRepository.values().stream()
                .toList();
    }

    void createMissionRepository(Mission... missions) {
        Stream.of(missions).forEach(mission -> sampleMissionRepository.put(mission.getId(), mission));
    }
}
