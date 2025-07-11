package com.example.repository;

import com.example.model.Rocket;

import java.util.List;
import java.util.UUID;

public interface RocketRepository {
    void addRocket(Rocket rocket);
    void deleteRocketById(UUID rocketId);
    void updateRocketById(UUID rocketId, Rocket rocket);
    Rocket getRocketById(UUID rocketId);
    List<Rocket> getAllRockets();
}
