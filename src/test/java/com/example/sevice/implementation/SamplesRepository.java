package com.example.sevice.implementation;

import com.example.model.Mission;
import com.example.model.Rocket;
import com.example.model.type.MissionStatus;
import com.example.model.type.RocketStatus;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

class SamplesRepository {

    final String rocketNameNo1 = "Falcon";
    final String rocketNameNo2 = "Dragon";
    final String rocketNameNo3 = "Phoenix";
    final String rocketNameNo4 = "Viper";
    final String rocketNameNo5 = "Raven";
    final String rocketNameNo6 = "Scorpion";

    final String missionNameNo1 = "Nebula";
    final String missionNameNo2 = "Vortex";
    final String missionNameNo3 = "Aegis";
    final String missionNameNo4 = "Nova";
    final String missionNameNo5 = "Pegasus";
    final String missionNameNo6 = "Cerberus";

    final Rocket rocketNo1 = Rocket.of(rocketNameNo1);
    final Rocket rocketNo2 = Rocket.of(rocketNameNo2);
    final Rocket rocketNo3 = Rocket.of(rocketNameNo3);
    final Rocket rocketNo4 = Rocket.of(rocketNameNo4);
    final Rocket rocketNo5 = Rocket.of(rocketNameNo5);
    final Rocket rocketNo6 = Rocket.of(rocketNameNo6);

    final Mission missionNo1 = Mission.of(missionNameNo1);
    final Mission missionNo2 = Mission.of(missionNameNo2);
    final Mission missionNo3 = Mission.of(missionNameNo3);
    final Mission missionNo4 = Mission.of(missionNameNo4);
    final Mission missionNo5 = Mission.of(missionNameNo5);
    final Mission missionNo6 = Mission.of(missionNameNo6);

    Rocket createRocket(Rocket rocket, RocketStatus status, UUID missionId) {
        rocket.setRocketStatus(status);
        rocket.setMissionId(missionId);
        return rocket;
    }

    Mission createMission(Mission mission, MissionStatus status, UUID... rocketIds) {
        mission.setMissionStatus(status);
        Stream.of(rocketIds).forEach(mission::addRocket);
        return mission;
    }

    List<Rocket> createRocketsList() {
        return List.of(
                createRocket(rocketNo1, RocketStatus.ON_GROUND, missionNo1.getId()),
                createRocket(rocketNo2, RocketStatus.IN_SPACE, missionNo1.getId()),
                createRocket(rocketNo3, RocketStatus.IN_SPACE, missionNo1.getId()),
                createRocket(rocketNo4, RocketStatus.IN_SPACE, missionNo2.getId()),
                createRocket(rocketNo5, RocketStatus.IN_REPAIR, missionNo2.getId()),
                createRocket(rocketNo6, RocketStatus.ON_GROUND, null)
        );
    }

    List<Mission> createMissionsList() {
        return List.of(
                createMission(missionNo1, MissionStatus.IN_PROGRESS, rocketNo1.getId(), rocketNo2.getId(), rocketNo3.getId()),
                createMission(missionNo2, MissionStatus.PENDING, rocketNo4.getId(), rocketNo5.getId()),
                createMission(missionNo3, MissionStatus.ENDED),
                createMission(missionNo4, MissionStatus.SCHEDULED),
                createMission(missionNo5, MissionStatus.SCHEDULED),
                createMission(missionNo6, MissionStatus.ENDED)
        );
    }

    final String summaryOutput = """
            • Nebula - In progress - Dragons: 3
                o Falcon - On ground
                o Dragon - In space
                o Phoenix - In space
            • Vortex - Pending - Dragons: 2
                o Viper - In space
                o Raven - In repair
            • Pegasus - Scheduled - Dragons: 0
            • Nova - Scheduled - Dragons: 0
            • Cerberus - Ended - Dragons: 0
            • Aegis - Ended - Dragons: 0
            """;
}
