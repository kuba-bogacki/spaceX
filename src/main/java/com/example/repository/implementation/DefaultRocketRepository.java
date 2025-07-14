package com.example.repository.implementation;

import com.example.model.Rocket;
import com.example.repository.RocketRepository;
import com.example.repository.exception.RocketRepositoryException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultRocketRepository implements RocketRepository {

    private static final String ROCKET_ENTITY_NOT_FOUND = "Rocket with id [%s] not found";

    private final Map<UUID, Rocket> rocketsDatabaseProvider;

    @Override
    public void addRocket(Rocket rocket) {
        rocketsDatabaseProvider.put(rocket.getId(), rocket);
    }

    @Override
    public void updateRocket(UUID rocketId, Rocket rocket) throws RocketRepositoryException {
        Optional.ofNullable(rocketsDatabaseProvider.get(rocketId))
                .map(rocketEntity -> rocketsDatabaseProvider.replace(rocketEntity.getId(), rocket))
                .orElseThrow(() -> new RocketRepositoryException(String.format(ROCKET_ENTITY_NOT_FOUND, rocketId)));
    }

    @Override
    public Rocket getRocketById(UUID rocketId) throws RocketRepositoryException {
        return Optional.ofNullable(rocketsDatabaseProvider.get(rocketId))
                .orElseThrow(() -> new RocketRepositoryException(String.format(ROCKET_ENTITY_NOT_FOUND, rocketId)));
    }

    @Override
    public List<Rocket> getAllRockets() {
        return rocketsDatabaseProvider.values().stream()
                .toList();
    }

    public static DefaultRocketRepository of(Map<UUID, Rocket> rocketsDatabaseProvider) {
        requireNonNull(rocketsDatabaseProvider, "Rockets database provider cannot be null");
        return new DefaultRocketRepository(rocketsDatabaseProvider);
    }
}
