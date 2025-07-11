package com.example.repository.implementation;

import com.example.exception.MissionRepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

public class MissionRepositoryImplementationTest extends SamplesRepository {

    private MissionRepositoryImplementation repository;

    @BeforeEach
    void setUp() {
        repository = MissionRepositoryImplementation.of(sampleMissionRepository);
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
    @DisplayName("Should delete mission from database")
    void test_02() {
        //given
        UUID uuid = missionNo1.getId();

        //when
        repository.deleteMissionById(uuid);

        //then
        assertThat(sampleMissionRepository.values())
                .hasSize(2);
    }

    @Test
    @DisplayName("Should update mission if mission with provided id exist in database")
    void test_03() {
        //given
        UUID uuid = missionNo1.getId();

        //when
        repository.updateMissionById(uuid, missionNo4);

        //then
        assertThat(sampleMissionRepository.get(uuid).getName())
                .isEqualTo(missionNo4.getName());
    }

    @Test
    @DisplayName("Should throw an exception if mission to update not exist in database")
    void test_04() {
        //given
        UUID randomUuid = UUID.randomUUID();

        //when
        final var expectedException = catchException(() -> repository.updateMissionById(randomUuid, missionNo4));

        //then
        assertThat(expectedException)
                .isInstanceOf(MissionRepositoryException.class);
    }

    @Test
    @DisplayName("Should return mission if mission exist in database")
    void test_05() {
        //given
        UUID uuid = missionNo3.getId();

        //when
        final var result = repository.getMissionById(uuid);

        //then
        assertThat(result)
                .isEqualTo(missionNo3);
    }

    @Test
    @DisplayName("Should return null if mission not exist in database")
    void test_06() {
        //given
        UUID uuid = missionNo4.getId();

        //when
        final var result = repository.getMissionById(uuid);

        //then
        assertThat(result)
                .isNull();
    }

    @Test
    @DisplayName("Should return mission list if some rockets exists in database")
    void test_07() {
        //when
        final var result = repository.getAllMissions();

        //then
        assertThat(result)
                .hasSize(3);
    }

    @Test
    @DisplayName("Should return empty list if no mission exist in database")
    void test_08() {
        //given
        repository = MissionRepositoryImplementation.of(new HashMap<>());

        //when
        final var result = repository.getAllMissions();

        //then
        assertThat(result)
                .isEmpty();
    }
}
