package com.github.aadvorak.artilleryonline.battle.statistics;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.entity.User;

import java.util.StringJoiner;

public class BattleStatisticsUtil {

    /**
     * todo temporal solution
     */
    public static String createUserStatisticsMsg(BattleModel battleModel, User user) {
        var msgBuilder = new StringJoiner(" ");
        if (battleModel.getVehicles().get(user.getNickname()) != null) {
            msgBuilder.add("Your vehicle survived.");
        } else {
            msgBuilder.add("Your vehicle destroyed.");
        }
        var statistics = battleModel.getStatistics().get(user.getId());
        if (statistics.getMadeShots() > 0) {
            msgBuilder.add(String.format("Made shots: %d.", statistics.getMadeShots()));
        }
        if (statistics.getDestroyedVehicles() > 0) {
            msgBuilder.add(String.format("Destroyed vehicles: %d.", statistics.getDestroyedVehicles()));
        }
        msgBuilder.add(String.format("Damage: caused %.2f / received %.2f.",
                statistics.getCausedDamage(), statistics.getReceivedDamage()));
        msgBuilder.add(String.format("Direct hits: caused %d / received %d.",
                statistics.getCausedDirectHits(), statistics.getReceivedDirectHits()));
        msgBuilder.add(String.format("Indirect hits: caused %d / received %d.",
                statistics.getCausedIndirectHits(), statistics.getReceivedIndirectHits()));
        msgBuilder.add(String.format("Track breaks: caused %d / received %d.",
                statistics.getCausedTrackBreaks(), statistics.getReceivedTrackBreaks()));
        return msgBuilder.toString();
    }
}
