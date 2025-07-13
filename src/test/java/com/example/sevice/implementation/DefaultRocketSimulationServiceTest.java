package com.example.sevice.implementation;

import com.example.model.Mission;
import com.example.model.Rocket;
import com.example.model.type.MissionStatus;
import com.example.model.type.RocketStatus;
import com.example.repository.exception.MissionRepositoryException;
import com.example.repository.exception.RocketRepositoryException;
import com.example.sevice.exception.SpaceXDragonException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.assertAll;

class DefaultRocketSimulationServiceTest extends SamplesRepository {

    private FakeMissionRepository missionRepository;
    private FakeRocketRepository rocketRepository;
    private DefaultRocketSimulationService rocketSimulationService;

    @BeforeEach
    void setUp() {
        missionRepository = new FakeMissionRepository();
        rocketRepository = new FakeRocketRepository();
        rocketSimulationService = DefaultRocketSimulationService.of(missionRepository, rocketRepository);
    }

    @Test
    @DisplayName("Should add new rocket to repository with provided name")
    void test_01() {
        //when
        rocketSimulationService.addNewRocket(rocketNameNo1);

        //then
        assertAll(
                () -> assertThat(rocketRepository.getSpyRocket().getName()).isEqualTo(rocketNameNo1),
                () -> assertThat(rocketRepository.getSpyRocket().getRocketStatus()).isEqualTo(RocketStatus.ON_GROUND),
                () -> assertThat(rocketRepository.getAllRockets().size()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("Should assign rocket to mission and update mission status if mission exist")
    void test_02() {
        //given
        rocketRepository.createRocketRepository(rocketNo1);
        missionRepository.createMissionRepository(missionNo1);

        //when
        rocketSimulationService.assignRocketToMission(rocketNo1.getId(), missionNo1.getId());

        //then
        assertAll(
                () -> assertThat(rocketRepository.getRocketById(rocketNo1.getId()).getRocketStatus()).isEqualTo(RocketStatus.IN_SPACE),
                () -> assertThat(rocketRepository.getRocketById(rocketNo1.getId()).getMissionId()).isEqualTo(missionNo1.getId()),
                () -> assertThat(missionRepository.getMissionById(missionNo1.getId()).getRocketList().size()).isEqualTo(1),
                () -> assertThat(missionRepository.getMissionById(missionNo1.getId()).getMissionStatus()).isEqualTo(MissionStatus.IN_PROGRESS)
        );
    }

    @Test
    @DisplayName("Should assign rocket to mission and update mission status to 'pending' if one of rocket is in repair")
    void test_03() {
        //given
        rocketNo2.setRocketStatus(RocketStatus.IN_REPAIR);
        rocketNo2.setMissionId(missionNo2.getId());
        missionNo1.addRocket(rocketNo2.getId());

        rocketRepository.createRocketRepository(rocketNo1, rocketNo2);
        missionRepository.createMissionRepository(missionNo1, missionNo2);

        //when
        rocketSimulationService.assignRocketToMission(rocketNo1.getId(), missionNo1.getId());

        //then
        assertAll(
                () -> assertThat(rocketRepository.getRocketById(rocketNo1.getId()).getRocketStatus()).isEqualTo(RocketStatus.IN_SPACE),
                () -> assertThat(missionRepository.getMissionById(missionNo1.getId()).getMissionStatus()).isEqualTo(MissionStatus.PENDING)
        );
    }

    @Test
    @DisplayName("Should throw an exception if rocket is already assign to mission")
    void test_04() {
        //given
        rocketNo1.setMissionId(missionNo1.getId());
        missionNo1.addRocket(rocketNo1.getId());

        rocketRepository.createRocketRepository(rocketNo1, rocketNo2);
        missionRepository.createMissionRepository(missionNo1, missionNo2);

        //when
        final var expectedException =
                catchException(() -> rocketSimulationService.assignRocketToMission(rocketNo1.getId(), missionNo1.getId()));

        //then
        assertThat(expectedException)
                .isInstanceOf(SpaceXDragonException.class)
                .hasMessageContaining("Current rocket is already assigned to other mission");
    }

    @Test
    @DisplayName("Should throw an exception if rocket with provided id is not exist in database")
    void test_05() {
        //given
        rocketRepository = new FakeRocketRepository() {
            @Override
            public Rocket getRocketById(UUID rocketId) throws RocketRepositoryException {
                throw new RocketRepositoryException(String.format("Rocket with id [%s] not found", rocketId));
            }
        };
        rocketSimulationService = DefaultRocketSimulationService.of(missionRepository, rocketRepository);

        //when
        final var expectedException =
                catchException(() -> rocketSimulationService.assignRocketToMission(rocketNo1.getId(), missionNo1.getId()));

        //then
        assertThat(expectedException)
                .isInstanceOf(SpaceXDragonException.class)
                .hasMessageContaining(String.format("Rocket with id [%s] not found", rocketNo1.getId()));
    }

    @Test
    @DisplayName("Should throw an exception if mission with provided id is not exist in database")
    void test_06() {
        //given
        missionRepository = new FakeMissionRepository() {
            @Override
            public Mission getMissionById(UUID missionId) throws MissionRepositoryException {
                throw new MissionRepositoryException(String.format("Mission with id [%s] not found", missionId));
            }
        };
        rocketSimulationService = DefaultRocketSimulationService.of(missionRepository, rocketRepository);

        //when
        final var expectedException =
                catchException(() -> rocketSimulationService.assignRocketToMission(rocketNo1.getId(), missionNo1.getId()));

        //then
        assertThat(expectedException)
                .isInstanceOf(SpaceXDragonException.class)
                .hasMessageContaining(String.format("Mission with id [%s] not found", missionNo1.getId()));
    }

    @Test
    @DisplayName("Should change rocket status to 'in space' and mission status to 'in progress'")
    void test_07() {
        //given
        rocketNo1.setRocketStatus(RocketStatus.IN_REPAIR);
        rocketNo1.setMissionId(missionNo1.getId());
        missionNo1.addRocket(rocketNo1.getId());

        rocketRepository.createRocketRepository(rocketNo1);
        missionRepository.createMissionRepository(missionNo1);

        //when
        rocketSimulationService.changeRocketStatus(rocketNo1.getId(), RocketStatus.IN_SPACE);

        //then
        assertAll(
                () -> assertThat(rocketRepository.getSpyRocket().getRocketStatus()).isEqualTo(RocketStatus.IN_SPACE),
                () -> assertThat(missionRepository.getSpyMission().getMissionStatus()).isEqualTo(MissionStatus.IN_PROGRESS)
        );
    }

    @Test
    @DisplayName("Should change rocket status to 'in repair' and mission status to 'pending'")
    void test_08() {
        //given
        rocketNo1.setRocketStatus(RocketStatus.IN_SPACE);
        rocketNo1.setMissionId(missionNo1.getId());
        missionNo1.addRocket(rocketNo1.getId());

        rocketRepository.createRocketRepository(rocketNo1);
        missionRepository.createMissionRepository(missionNo1);

        //when
        rocketSimulationService.changeRocketStatus(rocketNo1.getId(), RocketStatus.IN_REPAIR);

        //then
        assertAll(
                () -> assertThat(rocketRepository.getSpyRocket().getRocketStatus()).isEqualTo(RocketStatus.IN_REPAIR),
                () -> assertThat(missionRepository.getSpyMission().getMissionStatus()).isEqualTo(MissionStatus.PENDING)
        );
    }

    @Test
    @DisplayName("Shouldn't update rocket status if mission id is null")
    void test_09() {
        //given
        rocketRepository.createRocketRepository(rocketNo1);
        missionRepository.createMissionRepository(missionNo1);

        //when
        rocketSimulationService.changeRocketStatus(rocketNo1.getId(), RocketStatus.IN_REPAIR);

        //then
        assertAll(
                () -> assertThat(rocketRepository.isRocketUpdated()).isFalse(),
                () -> assertThat(missionRepository.isMissionUpdated()).isFalse()
        );
    }

    @Test
    @DisplayName("Should throw an exception if rocket try to be assigned to another mission")
    void test_10() {
        //given
        rocketNo1.setRocketStatus(RocketStatus.ON_GROUND);
        rocketNo1.setMissionId(missionNo1.getId());

        rocketRepository.createRocketRepository(rocketNo1);
        missionRepository.createMissionRepository(missionNo1);

        //when
        final var expectedException =
                catchException(() -> rocketSimulationService.changeRocketStatus(rocketNo1.getId(), RocketStatus.IN_SPACE));

        //then
        assertThat(expectedException)
                .isInstanceOf(SpaceXDragonException.class)
                .hasMessageContaining("Current rocket should not be assigned anymore to a mission");
    }

    @Test
    @DisplayName("Should throw an exception if rocket with provided id is not exist in database")
    void test_11() {
        //given
        rocketRepository = new FakeRocketRepository() {
            @Override
            public Rocket getRocketById(UUID rocketId) throws RocketRepositoryException {
                throw new RocketRepositoryException(String.format("Rocket with id [%s] not found", rocketId));
            }
        };
        rocketSimulationService = DefaultRocketSimulationService.of(missionRepository, rocketRepository);

        //when
        final var expectedException =
                catchException(() -> rocketSimulationService.changeRocketStatus(rocketNo1.getId(), RocketStatus.IN_SPACE));

        //then
        assertThat(expectedException)
                .isInstanceOf(SpaceXDragonException.class)
                .hasMessageContaining(String.format("Rocket with id [%s] not found", rocketNo1.getId()));
    }

    @Test
    @DisplayName("Should throw an exception if mission with provided id is not exist in database")
    void test_12() {
        //given
        rocketNo1.setRocketStatus(RocketStatus.IN_SPACE);
        rocketNo1.setMissionId(missionNo1.getId());
        missionNo1.addRocket(rocketNo1.getId());

        rocketRepository.createRocketRepository(rocketNo1);

        missionRepository = new FakeMissionRepository() {
            @Override
            public Mission getMissionById(UUID missionId) throws MissionRepositoryException {
                throw new MissionRepositoryException(String.format("Mission with id [%s] not found", missionId));
            }
        };
        rocketSimulationService = DefaultRocketSimulationService.of(missionRepository, rocketRepository);

        //when
        final var expectedException =
                catchException(() -> rocketSimulationService.changeRocketStatus(rocketNo1.getId(), RocketStatus.IN_REPAIR));

        //then
        assertThat(expectedException)
                .isInstanceOf(SpaceXDragonException.class)
                .hasMessageContaining(String.format("Mission with id [%s] not found", missionNo1.getId()));
    }
}
