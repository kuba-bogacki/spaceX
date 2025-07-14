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

import java.util.*;

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
                    MissionStatus.SCHEDULED : isSomeRocketInRepair(mission.getId()) ?
                            MissionStatus.PENDING : MissionStatus.IN_PROGRESS);
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
        try {
            var mission = missionRepository.getMissionById(missionId);
            if (mission.getMissionStatus() == MissionStatus.ENDED && isBeforeEndedMissionStatus(missionStatus)) {
                throw new MissionSimulationServiceException("Impossible to change mission status to previous state");
            }

            if (mission.getMissionStatus() == MissionStatus.SCHEDULED && isInMissionStatus(missionStatus)) {
                throw new MissionSimulationServiceException("Impossible to change status, no rocket assigned to mission");
            }

            if (isInMissionStatus(mission.getMissionStatus())) {
                for (var rocket : getRocketOnMission(mission.getId())) {
                    rocket.setRocketStatus(RocketStatus.ON_GROUND);
                    rocketRepository.updateRocket(rocket.getId(), rocket);
                }
            }

            mission.setMissionStatus(missionStatus);
            mission.clearRocketList();
            missionRepository.updateMission(mission.getId(), mission);
        } catch (RocketRepositoryException | MissionRepositoryException | MissionSimulationServiceException exception) {
            throw new SpaceXDragonException(String.format("Error due changing mission status: %s", exception.getMessage()));
        }
    }

    private boolean isBeforeEndedMissionStatus(MissionStatus status) {
        return EnumSet.of(MissionStatus.IN_PROGRESS, MissionStatus.PENDING, MissionStatus.SCHEDULED).contains(status);
    }

    private boolean isInMissionStatus(MissionStatus status) {
        return EnumSet.of(MissionStatus.IN_PROGRESS, MissionStatus.PENDING).contains(status);
    }

    private List<Rocket> getRocketOnMission(UUID missionId) {
        return rocketRepository.getAllRockets().stream()
                .filter(rocket -> rocket.getMissionId().equals(missionId))
                .toList();
    }

    @Override
    public String getSummaryMissions() {
        var stringBuilder = new StringBuilder();
        var rockets = rocketRepository.getAllRockets();
        var sortedMissions = missionRepository.getAllMissions().stream()
                .sorted(Comparator
                        .comparing((Mission mission) -> mission.getRocketList().size())
                        .thenComparing(Mission::getName).reversed())
                .toList();

        for (var mission : sortedMissions) {
            stringBuilder
                    .append(mission.toString())
                    .append("\n");
            mission.getRocketList()
                    .forEach(rocketId -> stringBuilder
                            .append(getRocketRepresentation(rockets, rocketId, mission.getId()))
                            .append("\n"));
        }
        return stringBuilder.toString();
    }

    private String getRocketRepresentation(List<Rocket> rockets, UUID rocketId, UUID missionId) {
        return requireNonNull(rockets.stream()
                .filter(rocket -> rocket.getId().equals(rocketId))
                .filter(rocket -> rocket.getMissionId().equals(missionId))
                .findAny()
                .orElseThrow()
                .toString());
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
