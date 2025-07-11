package com.example.repository.implementation;

import com.example.model.Rocket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class RepositorySamples {

    final String rocketNameNo1 = "Falcon";
    final String rocketNameNo2 = "Dragon";
    final String rocketNameNo3 = "Phoenix";
    final String rocketNameNo4 = "Viper";

    final Rocket rocketNo1 = Rocket.of(rocketNameNo1);
    final Rocket rocketNo2 = Rocket.of(rocketNameNo2);
    final Rocket rocketNo3 = Rocket.of(rocketNameNo3);
    final Rocket rocketNo4 = Rocket.of(rocketNameNo4);

    Map<UUID, Rocket> sampleRepository =  new HashMap<>() {{
        put(rocketNo1.getId(), rocketNo1);
        put(rocketNo2.getId(), rocketNo2);
        put(rocketNo3.getId(), rocketNo3);
    }};
}
