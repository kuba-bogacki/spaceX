package com.example.repository.implementation;

import com.example.exception.RocketRepositoryException;
import com.example.model.Rocket;
import com.example.repository.RocketRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RocketRepositoryImplementation implements RocketRepository {

    private final Map<UUID, Rocket> rocketsDatabaseProvider;

    @Override
    public void addRocket(Rocket rocket) {
        this.rocketsDatabaseProvider.put(rocket.getId(), rocket);
    }

    @Override
    public void deleteRocketById(UUID rocketId) {
        this.rocketsDatabaseProvider.remove(rocketId);
    }

    @Override
    public void updateRocketById(UUID rocketId, Rocket rocket) {
        var rocketEntity = this.rocketsDatabaseProvider.get(rocketId);
        if (rocketEntity == null) {
            throw new RocketRepositoryException(String.format("Impossible to find rocket with provided id: %s", rocketId));
        }
        this.rocketsDatabaseProvider.replace(rocketId, rocket);
    }

    @Override
    public Rocket getRocketById(UUID rocketId) {
        return this.rocketsDatabaseProvider.get(rocketId);
    }

    @Override
    public List<Rocket> getAllRockets() {
        return this.rocketsDatabaseProvider.values().stream()
                .toList();
    }

    public static RocketRepositoryImplementation of(Map<UUID, Rocket> rocketsDatabaseProvider) {
        requireNonNull(rocketsDatabaseProvider, "Rockets database provider cannot be null");
        return new RocketRepositoryImplementation(rocketsDatabaseProvider);
    }
}
