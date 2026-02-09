package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.BattleParticipant;
import com.github.aadvorak.artilleryonline.battle.BattleParticipantParams;
import com.github.aadvorak.artilleryonline.battle.preset.VehicleSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BotsService {

    private static final List<String> BOT_NICKNAMES = List.of(
            "Leon",
            "Markoyo",
            "Picore",
            "Fervor",
            "Jimmy",
            "Kirileyson",
            "Ciri",
            "CallMeBot",
            "LanceBot",
            "YoloBot",
            "Noob",
            "Worm",
            "LadyNemesis"
    );

    private static final List<String> BOT_COLORS = List.of(
            "#ff5733",
            "#ffd733",
            "#ff9633",
            "#bf04ec",
            "#5dfc02",
            "#02f4fc",
            "#fc0688"
    );

    public BattleParticipant generateBot(Set<BattleParticipant> participants) {
        var bot = new BattleParticipant();
        fillBot(bot, participants);
        return bot;
    }

    public void fillBot(BattleParticipant bot, Set<BattleParticipant> participants) {
        bot.setNickname(makeUniqueNickname(getRandomNickname(), participants));
        bot.setParams(new BattleParticipantParams()
                .setSelectedVehicle(getRandomVehicle())
                .setVehicleColor(getRandomColor()));
    }

    public String getRandomVehicle() {
        var vehicles = Arrays.stream(VehicleSpecsPreset.values()).map(VehicleSpecsPreset::getName).toList();
        return vehicles.get(BattleUtils.generateRandom(0, vehicles.size()));
    }

    private String getRandomNickname() {
        return BOT_NICKNAMES.get(BattleUtils.generateRandom(0, BOT_NICKNAMES.size()));
    }

    private String getRandomColor() {
        return BOT_COLORS.get(BattleUtils.generateRandom(0, BOT_COLORS.size()));
    }

    private String makeUniqueNickname(String nickname, Set<BattleParticipant> participants) {
        var existingNicknames = participants.stream()
                .map(BattleParticipant::getNickname)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        while (existingNicknames.contains(nickname)) {
            nickname += "1";
        }
        return nickname;
    }
}
