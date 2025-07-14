package com.example.repository.implementation;

import com.example.model.type.MissionStatus;
import com.example.repository.exception.MissionRepositoryException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class DefaultMissionRepositoryTest extends SamplesRepository {

    private DefaultMissionRepository repository;

    @BeforeEach
    void setUp() {
        repository = DefaultMissionRepository.of(sampleMissionRepository);
    }

    @Test
    @DisplayName("Should add new mission to database")
    void test_01() {
        //when
        repository.addMission(missionNo4);

        //then
        assertThat(sampleMissionRepository.values())
                .hasSize(4);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should update mission if mission with provided id exist in database")
    void test_02() {
        //given
        UUID uuid = missionNo1.getId();

        //when
        repository.updateMission(uuid, missionNo4);

        //then
        assertThat(sampleMissionRepository.get(uuid).getName())
                .isEqualTo(missionNo4.getName());
    }

    @Test
    @DisplayName("Should throw an exception if mission to update not exist in database")
    void test_03() {
        //given
        UUID randomUuid = UUID.randomUUID();

        //when
        final var expectedException = catchException(() -> repository.updateMission(randomUuid, missionNo4));

        //then
        assertThat(expectedException)
                .isInstanceOf(MissionRepositoryException.class);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return mission if mission exist in database")
    void test_04() {
        //given
        UUID uuid = missionNo3.getId();

        //when
        final var result = repository.getMissionById(uuid);

        //then
        assertThat(result)
                .isEqualTo(missionNo3);
    }

    @Test
    @DisplayName("Should throw an exception if mission not exist in database")
    void test_05() {
        //given
        UUID uuid = missionNo4.getId();

        //when
        final var result = catchException(() -> repository.getMissionById(uuid));

        //then
        assertThat(result)
                .isInstanceOf(MissionRepositoryException.class);
    }

    @Test
    @DisplayName("Should return mission list if some rockets exists in database")
    void test_06() {
        //when
        final var result = repository.getAllMissions();

        //then
        assertThat(result)
                .hasSize(3);
    }

    @Test
    @DisplayName("Should return empty list if no mission exist in database")
    void test_07() {
        //given
        repository = DefaultMissionRepository.of(new HashMap<>());

        //when
        final var result = repository.getAllMissions();

        //then
        assertThat(result)
                .isEmpty();
    }

    @Test
    @SneakyThrows
    @DisplayName("Should create mission with default 'scheduled' status")
    void test_08() {
        //when
        repository.addMission(missionNo1);

        //then
        assertThat(repository.getMissionById(missionNo1.getId()).getMissionStatus())
                .isEqualTo(MissionStatus.SCHEDULED);
    }
}
