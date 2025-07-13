package com.example.repository.implementation;

import com.example.repository.exception.RocketRepositoryException;
import com.example.model.type.RocketStatus;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class RocketRepositoryImplementationTest extends SamplesRepository {

    private RocketRepositoryImplementation repository;

    @BeforeEach
    void setUp() {
        repository = RocketRepositoryImplementation.of(sampleRocketRepository);
    }

    @Test
    @DisplayName("Should add new rocket to database")
    void test_01() {
        //when
        repository.addRocket(rocketNo4);

        //then
        assertThat(sampleRocketRepository.values())
                .hasSize(4);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should update rocket if rocket with provided id exist in database")
    void test_02() {
        //given
        UUID uuid = rocketNo1.getId();

        //when
        repository.updateRocket(uuid, rocketNo4);

        //then
        assertThat(sampleRocketRepository.get(uuid).getName())
                .isEqualTo(rocketNo4.getName());
    }

    @Test
    @DisplayName("Should throw an exception if rocket to update not exist in database")
    void test_03() {
        //given
        UUID randomUuid = UUID.randomUUID();

        //when
        final var expectedException = catchException(() -> repository.updateRocket(randomUuid, rocketNo4));

        //then
        assertThat(expectedException)
                .isInstanceOf(RocketRepositoryException.class);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return rocket if rocket exist in database")
    void test_04() {
        //given
        UUID uuid = rocketNo3.getId();

        //when
        final var result = repository.getRocketById(uuid);

        //then
        assertThat(result)
                .isEqualTo(rocketNo3);
    }

    @Test
    @DisplayName("Should throw an exception if rocket not exist in database")
    void test_05() {
        //given
        UUID uuid = rocketNo4.getId();

        //when
        final var result = catchException(() -> repository.getRocketById(uuid));

        //then
        assertThat(result)
                .isInstanceOf(RocketRepositoryException.class);
    }

    @Test
    @DisplayName("Should return rocket list if some rockets exists in database")
    void test_06() {
        //when
        final var result = repository.getAllRockets();

        //then
        assertThat(result)
                .hasSize(3);
    }

    @Test
    @DisplayName("Should return empty list if no rocket exist in database")
    void test_07() {
        //given
        repository = RocketRepositoryImplementation.of(new HashMap<>());

        //when
        final var result = repository.getAllRockets();

        //then
        assertThat(result)
                .isEmpty();
    }

    @Test
    @SneakyThrows
    @DisplayName("Should create rocket with default 'on ground' status")
    void test_08() {
        //when
        repository.addRocket(rocketNo1);

        //then
        assertThat(repository.getRocketById(rocketNo1.getId()).getRocketStatus())
                    .isEqualTo(RocketStatus.ON_GROUND);
    }
}
