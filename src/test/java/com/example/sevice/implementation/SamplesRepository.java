package com.example.sevice.implementation;

import com.example.model.Mission;
import com.example.model.Rocket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class SamplesRepository {

    final String rocketNameNo1 = "Falcon";
    final String rocketNameNo2 = "Dragon";
    final String rocketNameNo3 = "Phoenix";
    final String rocketNameNo4 = "Viper";

    final String missionNameNo1 = "Falcon";
    final String missionNameNo2 = "Dragon";
    final String missionNameNo3 = "Phoenix";
    final String missionNameNo4 = "Viper";

    final Rocket rocketNo1 = Rocket.of(rocketNameNo1);
    final Rocket rocketNo2 = Rocket.of(rocketNameNo2);
    final Rocket rocketNo3 = Rocket.of(rocketNameNo3);
    final Rocket rocketNo4 = Rocket.of(rocketNameNo4);

    final Mission missionNo1 = Mission.of(missionNameNo1);
    final Mission missionNo2 = Mission.of(missionNameNo2);
    final Mission missionNo3 = Mission.of(missionNameNo3);
    final Mission missionNo4 = Mission.of(missionNameNo4);

    Map<UUID, Rocket> sampleRocketRepository =  new HashMap<>() {{
        put(rocketNo1.getId(), rocketNo1);
        put(rocketNo2.getId(), rocketNo2);
        put(rocketNo3.getId(), rocketNo3);
    }};

    Map<UUID, Mission> sampleMissionRepository =  new HashMap<>() {{
        put(missionNo1.getId(), missionNo1);
        put(missionNo2.getId(), missionNo2);
        put(missionNo3.getId(), missionNo3);
    }};
}
