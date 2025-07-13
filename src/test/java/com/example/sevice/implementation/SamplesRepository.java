package com.example.sevice.implementation;

import com.example.model.Mission;
import com.example.model.Rocket;

class SamplesRepository {

    final String rocketNameNo1 = "Falcon";
    final String rocketNameNo2 = "Dragon";
    final String rocketNameNo3 = "Phoenix";
    final String rocketNameNo4 = "Viper";

    final String missionNameNo1 = "Nebula";
    final String missionNameNo2 = "Vortex";
    final String missionNameNo3 = "Aegis";
    final String missionNameNo4 = "Nova";

    final Rocket rocketNo1 = Rocket.of(rocketNameNo1);
    final Rocket rocketNo2 = Rocket.of(rocketNameNo2);
    final Rocket rocketNo3 = Rocket.of(rocketNameNo3);
    final Rocket rocketNo4 = Rocket.of(rocketNameNo4);

    final Mission missionNo1 = Mission.of(missionNameNo1);
    final Mission missionNo2 = Mission.of(missionNameNo2);
    final Mission missionNo3 = Mission.of(missionNameNo3);
    final Mission missionNo4 = Mission.of(missionNameNo4);
}
