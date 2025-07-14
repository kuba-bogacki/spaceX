package com.example.sevice.implementation;

import com.example.model.Mission;
import com.example.model.Rocket;
import com.example.model.type.MissionStatus;
import com.example.model.type.RocketStatus;
import com.example.repository.MissionRepository;
import com.example.repository.RocketRepository;
import com.example.repository.exception.MissionRepositoryException;
import com.example.repository.exception.RocketRepositoryException;
import com.example.repository.implementation.DefaultMissionRepository;
import com.example.repository.implementation.DefaultRocketRepository;
import com.example.sevice.MissionSimulationService;
import com.example.sevice.exception.MissionSimulationServiceException;
import com.example.sevice.exception.SpaceXDragonException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultMissionSimulationService implements MissionSimulationService {

    private final MissionRepository missionRepository;
    private final RocketRepository rocketRepository;

    @Override
    public void addNewMission(String missionName) {
        requireNonNull(missionName, "Mission name cannot be null");
        var mission = Mission.of(missionName);
        missionRepository.addMission(mission);
    }

    @Override
    public void assignRocketsToTheMission(UUID missionId, UUID... rocketIds) {
        requireNonNull(missionId, "Mission id cannot be null");
        try {
            var mission = missionRepository.getMissionById(missionId);
            var rocketList = getRocketList(Arrays.asList(rocketIds));
            boolean someRocketOnMission = isSomeRocketOnMission(rocketList);

            if (someRocketOnMission) {
                throw new MissionSimulationServiceException("One of rocket is already assigned to other mission");
            }

            for (var rocket : rocketList) {
                mission.addRocket(rocket.getId());
                rocketRepository.updateRocket(rocket.getId(), setMissionToRocket(rocket, mission.getId()));
            }

            mission.setMissionStatus(mission.getRocketList().isEmpty() ?
                    MissionStatus.SCHEDULED :
                    isSomeRocketInRepair(mission.getId()) ?
                            MissionStatus.PENDING :
                            MissionStatus.IN_PROGRESS);
            missionRepository.updateMission(mission.getId(), mission);
        } catch (RocketRepositoryException | MissionRepositoryException | MissionSimulationServiceException exception) {
            throw new SpaceXDragonException(String.format("Error due assigning rockets to mission: %s", exception.getMessage()));
        }
    }

    private List<Rocket> getRocketList(List<UUID> rocketIdList) {
        return rocketRepository.getAllRockets().stream()
                .filter(rocket -> rocketIdList.contains(rocket.getId()))
                .toList();
    }

    private boolean isSomeRocketOnMission(List<Rocket> rocketList) {
        return rocketList.stream()
                .anyMatch(rocket -> rocket.getMissionId() != null);
    }

    private Rocket setMissionToRocket(Rocket rocket, UUID missionId) {
        rocket.setMissionId(missionId);
        rocket.setRocketStatus(RocketStatus.IN_SPACE);
        return rocket;
    }

    private boolean isSomeRocketInRepair(UUID missionId) {
        return rocketRepository.getAllRockets().stream()
                .filter(rocket -> rocket.getMissionId().equals(missionId))
                .anyMatch(rocket -> rocket.getRocketStatus() == RocketStatus.IN_REPAIR);
    }

    @Override
    public void changeMissionStatus(UUID missionId, MissionStatus missionStatus) {
        requireNonNull(missionId, "Mission id cannot be null");
        requireNonNull(missionStatus, "Mission status cannot be null");
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
