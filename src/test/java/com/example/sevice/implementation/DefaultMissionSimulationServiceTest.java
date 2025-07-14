package com.example.sevice.implementation;

import com.example.model.Mission;
import com.example.model.type.MissionStatus;
import com.example.model.type.RocketStatus;
import com.example.repository.exception.MissionRepositoryException;
import com.example.sevice.exception.SpaceXDragonException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.assertAll;

class DefaultMissionSimulationServiceTest extends SamplesRepository {

    private FakeMissionRepository missionRepository;
    private FakeRocketRepository rocketRepository;
    private DefaultMissionSimulationService missionSimulationService;

    @BeforeEach
    void setUp() {
        missionRepository = new FakeMissionRepository();
        rocketRepository = new FakeRocketRepository();
        missionSimulationService = DefaultMissionSimulationService.of(missionRepository, rocketRepository);
    }

    @Test
    @DisplayName("Should add new mission to repository with provided name")
    void test_01() {
        //when
        missionSimulationService.addNewMission(missionNameNo1);

        //then
        assertAll(
                () -> assertThat(missionRepository.getSpyMission().getName()).isEqualTo(missionNameNo1),
                () -> assertThat(missionRepository.getSpyMission().getMissionStatus()).isEqualTo(MissionStatus.SCHEDULED),
                () -> assertThat(missionRepository.getAllMissions().size()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("Should assign rockets to mission and update mission status if mission exist")
    void test_02() {
        //given
        rocketRepository.createRocketRepository(rocketNo1, rocketNo2);
        missionRepository.createMissionRepository(missionNo1);

        //when
        missionSimulationService.assignRocketsToTheMission(missionNo1.getId(), rocketNo1.getId(), rocketNo2.getId());

        //then
        assertAll(
                () -> assertThat(rocketRepository.getAllRockets()).allMatch(rocket -> rocket.getRocketStatus() == RocketStatus.IN_SPACE),
                () -> assertThat(rocketRepository.getAllRockets()).allMatch(rocket -> rocket.getMissionId() != null),
                () -> assertThat(missionRepository.getMissionById(missionNo1.getId()).getMissionStatus()).isEqualTo(MissionStatus.IN_PROGRESS)
        );
    }

    @Test
    @DisplayName("Should assign rockets to mission and update mission status to 'pending' if one of rocket is in repair")
    void test_03() {
        //given
        rocketNo1.setRocketStatus(RocketStatus.IN_REPAIR);
        rocketNo1.setMissionId(missionNo1.getId());
        missionNo1.addRocket(rocketNo1.getId());

        rocketRepository.createRocketRepository(rocketNo1, rocketNo2, rocketNo3);
        missionRepository.createMissionRepository(missionNo1);

        //when
        missionSimulationService.assignRocketsToTheMission(missionNo1.getId(), rocketNo2.getId(), rocketNo3.getId());

        //then
        assertAll(
                () -> assertThat(rocketRepository.getAllRockets()).allMatch(rocket -> rocket.getMissionId() != null),
                () -> assertThat(missionRepository.getMissionById(missionNo1.getId()).getMissionStatus()).isEqualTo(MissionStatus.PENDING)
        );
    }

    @Test
    @DisplayName("Should throw an exception if one of rocket is already assign to other mission")
    void test_04() {
        //given
        rocketNo1.setRocketStatus(RocketStatus.IN_SPACE);
        rocketNo1.setMissionId(missionNo2.getId());
        missionNo2.addRocket(rocketNo1.getId());

        rocketRepository.createRocketRepository(rocketNo1, rocketNo2);
        missionRepository.createMissionRepository(missionNo1, missionNo2);

        //when
        final var expectedException =
                catchException(() -> missionSimulationService.assignRocketsToTheMission(missionNo1.getId(), rocketNo1.getId(), rocketNo2.getId()));

        //then
        assertThat(expectedException)
                .isInstanceOf(SpaceXDragonException.class)
                .hasMessageContaining("One of rocket is already assigned to other mission");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should pass if rocket ids are empty")
    void test_05() {
        //given
        missionRepository.createMissionRepository(missionNo1);

        //when
        missionSimulationService.assignRocketsToTheMission(missionNo1.getId());

        //then
        assertThat(missionRepository.getMissionById(missionNo1.getId()).getMissionStatus())
                .isEqualTo(MissionStatus.SCHEDULED);
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
        missionSimulationService = DefaultMissionSimulationService.of(missionRepository, rocketRepository);

        //when
        final var expectedException =
                catchException(() -> missionSimulationService.assignRocketsToTheMission(missionNo1.getId(), rocketNo1.getId()));

        //then
        assertThat(expectedException)
                .isInstanceOf(SpaceXDragonException.class)
                .hasMessageContaining(String.format("Mission with id [%s] not found", missionNo1.getId()));
    }

    @Test
    @DisplayName("Should change mission to 'ended' and remove all rockets from mission and change it status to 'on ground'")
    void test_07() {
        //given
        rocketNo1.setRocketStatus(RocketStatus.IN_SPACE);
        rocketNo1.setMissionId(missionNo1.getId());
        rocketNo2.setRocketStatus(RocketStatus.IN_SPACE);
        rocketNo2.setMissionId(missionNo1.getId());
        missionNo1.addRocket(rocketNo1.getId());
        missionNo1.addRocket(rocketNo2.getId());
        missionNo1.setMissionStatus(MissionStatus.IN_PROGRESS);

        rocketRepository.createRocketRepository(rocketNo1, rocketNo2);
        missionRepository.createMissionRepository(missionNo1);

        //when
        missionSimulationService.changeMissionStatus(missionNo1.getId(), MissionStatus.ENDED);

        //then
        assertAll(
                () -> assertThat(rocketRepository.getAllRockets()).allMatch(rocket -> rocket.getMissionId() != null),
                () -> assertThat(rocketRepository.getAllRockets()).allMatch(rocket -> rocket.getRocketStatus() == RocketStatus.ON_GROUND),
                () -> assertThat(missionRepository.getMissionById(missionNo1.getId()).getMissionStatus()).isEqualTo(MissionStatus.ENDED),
                () -> assertThat(missionRepository.getMissionById(missionNo1.getId()).getRocketList()).isEmpty()
        );
    }

    @Test
    @DisplayName("Should change mission to 'ended' if previous state was 'scheduled'")
    void test_08() {
        //given
        missionNo1.setMissionStatus(MissionStatus.SCHEDULED);

        missionRepository.createMissionRepository(missionNo1);

        //when
        missionSimulationService.changeMissionStatus(missionNo1.getId(), MissionStatus.ENDED);

        //then
        assertAll(
                () -> assertThat(missionRepository.getMissionById(missionNo1.getId()).getMissionStatus()).isEqualTo(MissionStatus.ENDED),
                () -> assertThat(missionRepository.getMissionById(missionNo1.getId()).getRocketList()).isEmpty()
        );
    }

    @ParameterizedTest
    @EnumSource(value = MissionStatus.class, names = {"SCHEDULED", "PENDING", "IN_PROGRESS"})
    @DisplayName("Should throw an exception if current status is 'ended' and status to change is previous")
    void test_09(MissionStatus missionStatus) {
        //given
        missionNo1.setMissionStatus(MissionStatus.ENDED);

        missionRepository.createMissionRepository(missionNo1);

        //when
        final var expectedException =
                catchException(() -> missionSimulationService.changeMissionStatus(missionNo1.getId(), missionStatus));

        //then
        assertThat(expectedException)
                .isInstanceOf(SpaceXDragonException.class)
                .hasMessageContaining("Impossible to change mission status to previous state");
    }

    @ParameterizedTest
    @EnumSource(value = MissionStatus.class, names = {"PENDING", "IN_PROGRESS"})
    @DisplayName("Should throw an exception if try to change status to 'pending' or 'in progress' when the mission has not started yet")
    void test_10(MissionStatus missionStatus) {
        //given
        missionNo1.setMissionStatus(MissionStatus.SCHEDULED);

        missionRepository.createMissionRepository(missionNo1);

        //when
        final var expectedException =
                catchException(() -> missionSimulationService.changeMissionStatus(missionNo1.getId(), missionStatus));

        //then
        assertThat(expectedException)
                .isInstanceOf(SpaceXDragonException.class)
                .hasMessageContaining("Impossible to change status, no rocket assigned to mission");
    }

    @Test
    @DisplayName("Should throw an exception if mission name is null")
    void test_11() {
        //given
        String nullMissionName = null;

        //when
        final var expectedException =
                catchException(() -> missionSimulationService.addNewMission(nullMissionName));

        //then
        assertThat(expectedException)
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Mission name cannot be null");
    }

    @Test
    @DisplayName("Should throw an exception if mission id is null")
    void test_12() {
        //given
        UUID nullMissionUuid = null;

        //when
        final var expectedException =
                catchException(() -> missionSimulationService.assignRocketsToTheMission(nullMissionUuid));

        //then
        assertThat(expectedException)
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Mission id cannot be null");
    }

    @Test
    @DisplayName("Should throw an exception if mission status is null")
    void test_13() {
        //given
        MissionStatus nullMissionStatus = null;

        //when
        final var expectedException =
                catchException(() -> missionSimulationService.changeMissionStatus(missionNo1.getId(), nullMissionStatus));

        //then
        assertThat(expectedException)
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Mission status cannot be null");
    }

    @Test
    @DisplayName("Should return summary of missions by number of rockets assigned")
    void test_14() {
        //given
        rocketRepository.createRocketRepository(createRocketsList().iterator().next());
        missionRepository.createMissionRepository(createMissionsList().iterator().next());

        //when
        final var result = missionSimulationService.getSummaryMissions();

        //then
        assertThat(result)
                .isEqualTo(summaryOutput);
    }
}
