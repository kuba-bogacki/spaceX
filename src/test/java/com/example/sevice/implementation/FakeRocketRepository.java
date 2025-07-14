package com.example.sevice.implementation;

import com.example.model.Rocket;
import com.example.repository.RocketRepository;
import com.example.repository.exception.RocketRepositoryException;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Getter
class FakeRocketRepository extends SamplesRepository implements RocketRepository {

    private final Map<UUID, Rocket> sampleRocketRepository = new HashMap<>();
    private Rocket spyRocket;
    private boolean rocketUpdated;

    @Override
    public void addRocket(Rocket rocket) {
        this.spyRocket = rocket;
        sampleRocketRepository.put(rocket.getId(), rocket);
    }

    @Override
    public void updateRocket(UUID rocketId, Rocket rocket) {
        this.spyRocket = rocket;
        this.rocketUpdated = true;
        sampleRocketRepository.replace(rocketId, rocket);
    }

    @Override
    public Rocket getRocketById(UUID rocketId) throws RocketRepositoryException {
        return sampleRocketRepository.get(rocketId);
    }

    @Override
    public List<Rocket> getAllRockets() {
        return sampleRocketRepository.values().stream()
                .toList();
    }

    void createRocketRepository(Rocket... rockets) {
        Stream.of(rockets).forEach(rocket -> sampleRocketRepository.put(rocket.getId(), rocket));
    }
}
