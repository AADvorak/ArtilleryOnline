package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.BattleParticipant;
import com.github.aadvorak.artilleryonline.battle.BattleParticipantParams;
import com.github.aadvorak.artilleryonline.battle.preset.VehicleSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
            "LadyNemesis",
            "KV-2",
            "IS-3",
            "Tiger",
            "Panther",
            "T1",
            "MS-1",
            "M3 Lee",
            "Sherman",
            "Leopard"
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
        bot.setNickname(getRandomNickname(participants));
        bot.setParams(new BattleParticipantParams()
                .setSelectedVehicle(getRandomVehicle())
                .setVehicleColor(getRandomColor()));
    }

    public String getRandomVehicle() {
        var vehicles = Arrays.stream(VehicleSpecsPreset.values()).map(VehicleSpecsPreset::getName).toList();
        return vehicles.get(BattleUtils.generateRandom(0, vehicles.size()));
    }

    private String getRandomNickname(Set<BattleParticipant> participants) {
        var existingNicknames = participants.stream()
                .map(BattleParticipant::getNickname)
                .filter(Objects::nonNull)
                .toList();
        var freeNicknames = BOT_NICKNAMES.stream()
                .filter(nickname -> !existingNicknames.contains(nickname))
                .toList();
        if (!freeNicknames.isEmpty()) {
            return freeNicknames.get(BattleUtils.generateRandom(0, freeNicknames.size()));
        }
        var nickname = BOT_NICKNAMES.get(BattleUtils.generateRandom(0, BOT_NICKNAMES.size()));
        while (existingNicknames.contains(nickname)) {
            nickname += "1";
        }
        return nickname;
    }

    public String getRandomColor() {
        return BOT_COLORS.get(BattleUtils.generateRandom(0, BOT_COLORS.size()));
    }
}
