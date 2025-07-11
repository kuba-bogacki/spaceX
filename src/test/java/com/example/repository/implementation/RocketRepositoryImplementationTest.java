package com.example.repository.implementation;

import com.example.exception.RocketRepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class RocketRepositoryImplementationTest extends RepositorySamples {

    private RocketRepositoryImplementation repository;

    @BeforeEach
    void setUp() {
        repository = RocketRepositoryImplementation.of(sampleRepository);
    }

    @Test
    @DisplayName("Should add new rocket to database")
    void test_01() {
        //when
        repository.addRocket(rocketNo4);

        //then
        assertThat(sampleRepository.values())
                .hasSize(4);
    }

    @Test
    @DisplayName("Should delete rocket from database")
    void test_02() {
        //given
        UUID uuid = rocketNo1.getId();

        //when
        repository.deleteRocketById(uuid);

        //then
        assertThat(sampleRepository.values())
                .hasSize(2);
    }

    @Test
    @DisplayName("Should update rocket if rocket with provided id exist in database")
    void test_03() {
        //given
        UUID uuid = rocketNo1.getId();

        //when
        repository.updateRocketById(uuid, rocketNo4);

        //then
        assertThat(sampleRepository.get(uuid).getName())
                .isEqualTo(rocketNo4.getName());
    }

    @Test
    @DisplayName("Should throw an exception if rocket to update not exist in database")
    void test_04() {
        //given
        UUID randomUuid = UUID.randomUUID();

        //when
        final var expectedException = catchException(() -> repository.updateRocketById(randomUuid, rocketNo4));

        //then
        assertThat(expectedException)
                .isInstanceOf(RocketRepositoryException.class);
    }

    @Test
    @DisplayName("Should return rocket if rocket exist in database")
    void test_05() {
        //given
        UUID uuid = rocketNo3.getId();

        //when
        final var result = repository.getRocketById(uuid);

        //then
        assertThat(result)
                .isEqualTo(rocketNo3);
    }

    @Test
    @DisplayName("Should return null if rocket not exist in database")
    void test_06() {
        //given
        UUID uuid = rocketNo4.getId();

        //when
        final var result = repository.getRocketById(uuid);

        //then
        assertThat(result)
                .isNull();
    }

    @Test
    @DisplayName("Should return rocket list if some rockets exists in database")
    void test_07() {
        //when
        final var result = repository.getAllRockets();

        //then
        assertThat(result)
                .hasSize(3);
    }

    @Test
    @DisplayName("Should return empty list if no rocket exist in database")
    void test_08() {
        //given
        repository = RocketRepositoryImplementation.of(new HashMap<>());

        //when
        final var result = repository.getAllRockets();

        //then
        assertThat(result)
                .isEmpty();
    }
}
