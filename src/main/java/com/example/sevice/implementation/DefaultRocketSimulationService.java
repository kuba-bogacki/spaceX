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
import com.example.sevice.RocketSimulationService;
import com.example.sevice.exception.RocketSimulationServiceException;
import com.example.sevice.exception.SpaceXDragonException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultRocketSimulationService implements RocketSimulationService {

    private final MissionRepository missionRepository;
    private final RocketRepository rocketRepository;

    @Override
    public void addNewRocket(String rocketName) {
        requireNonNull(rocketName, "Rocket name cannot be null");
        var rocket = Rocket.of(rocketName);
        rocketRepository.addRocket(rocket);
    }

    @Override
    public void assignRocketToMission(UUID rocketId, UUID missionId) {
        requireNonNull(rocketId, "Rocket id cannot be null");
        requireNonNull(missionId, "Mission id cannot be null");
        try {
            var rocket = rocketRepository.getRocketById(rocketId);
            var mission = missionRepository.getMissionById(missionId);

            if (rocket.getMissionId() != null) {
                throw new RocketSimulationServiceException("Current rocket is already assigned to other mission");
            }
            rocket.setRocketStatus(RocketStatus.IN_SPACE);
            rocket.setMissionId(missionId);

            mission.addRocket(rocket.getId());
            rocketRepository.updateRocket(rocket.getId(), rocket);

            boolean someRocketInRepair = isSomeRocketInRepair(mission);
            mission.setMissionStatus(someRocketInRepair ? MissionStatus.PENDING : MissionStatus.IN_PROGRESS);
            missionRepository.updateMission(mission.getId(), mission);
        } catch (RocketRepositoryException | MissionRepositoryException | RocketSimulationServiceException exception) {
            throw new SpaceXDragonException(String.format("Error due assigning rocket to mission: %s", exception.getMessage()));
        }
    }

    private boolean isSomeRocketInRepair(Mission mission) {
        return rocketRepository.getAllRockets().stream()
                .filter(rocket -> mission.getRocketList().contains(rocket.getId()))
                .anyMatch(rocket -> rocket.getRocketStatus() == RocketStatus.IN_REPAIR);
    }

    @Override
    public void changeRocketStatus(UUID rocketId, RocketStatus rocketStatus) {
        requireNonNull(rocketId, "Rocket id cannot be null");
        requireNonNull(rocketStatus, "Rocket status cannot be null");
        try {
            var rocket = rocketRepository.getRocketById(rocketId);

            if (rocket.getMissionId() != null) {
                if (rocket.getRocketStatus() == RocketStatus.ON_GROUND) {
                    throw new RocketSimulationServiceException("Current rocket should not be assigned anymore to a mission");
                }
                var mission = missionRepository.getMissionById(rocket.getMissionId());

                if (rocketStatus == RocketStatus.IN_REPAIR) {
                    mission.setMissionStatus(MissionStatus.PENDING);
                }

                if (rocketStatus == RocketStatus.IN_SPACE) {
                    mission.setMissionStatus(MissionStatus.IN_PROGRESS);
                }

                rocket.setRocketStatus(rocketStatus);
                rocketRepository.updateRocket(rocket.getId(), rocket);
                missionRepository.updateMission(mission.getId(), mission);
            }
        } catch (RocketRepositoryException | MissionRepositoryException | RocketSimulationServiceException exception) {
            throw new SpaceXDragonException(String.format("Error due changing rocket status: %s", exception.getMessage()));
        }
    }

    public static DefaultRocketSimulationService prod() {
        return DefaultRocketSimulationService.builder()
                .missionRepository(DefaultMissionRepository.of(new HashMap<>()))
                .rocketRepository(DefaultRocketRepository.of(new HashMap<>()))
                .build();
    }

    public static DefaultRocketSimulationService of(MissionRepository missionRepository, RocketRepository rocketRepository) {
        requireNonNull(missionRepository, "Mission repository cannot be null");
        requireNonNull(rocketRepository,  "Rocket repository cannot be null");
        return DefaultRocketSimulationService.builder()
                .missionRepository(missionRepository)
                .rocketRepository(rocketRepository)
                .build();
    }
}
