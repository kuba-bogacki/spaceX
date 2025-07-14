package com.example.repository;

import com.example.model.Rocket;
import com.example.repository.exception.RocketRepositoryException;

import java.util.List;
import java.util.UUID;

public interface RocketRepository {
    void addRocket(Rocket rocket);
    void updateRocket(UUID rocketId, Rocket rocket) throws RocketRepositoryException;
    Rocket getRocketById(UUID rocketId) throws RocketRepositoryException;
    List<Rocket> getAllRockets();
}
