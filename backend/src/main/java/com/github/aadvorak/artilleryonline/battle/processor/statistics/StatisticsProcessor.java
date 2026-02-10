package com.github.aadvorak.artilleryonline.battle.processor.statistics;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;

public class StatisticsProcessor {

    public static void increaseDestroyedDrones(String nickname, BattleCalculations battle) {
        if (nickname != null) {
            battle.getModel().getStatistics().get(nickname).increaseDestroyedDrones();
        }
    }

    public static void increaseDestroyedMissiles(String nickname, BattleCalculations battle) {
        if (nickname != null) {
            battle.getModel().getStatistics().get(nickname).increaseDestroyedMissiles();
        }
    }

    public static void increaseDamage(double damage, String receiverNickname,
                                      String causerNickname, BattleCalculations battle) {
        if (receiverNickname != null) {
            battle.getModel().getStatistics().get(receiverNickname).increaseReceivedDamage(damage);
        }
        if (causerNickname != null && battle.allowedTarget(causerNickname, receiverNickname)) {
            battle.getModel().getStatistics().get(causerNickname).increaseCausedDamage(damage);
        }
    }

    public static void increaseTrackBreaks(String receiverNickname, String causerNickname, BattleCalculations battle) {
        if (receiverNickname != null) {
            battle.getModel().getStatistics().get(receiverNickname).increaseReceivedTrackBreaks();
        }
        if (causerNickname != null && battle.allowedTarget(causerNickname, receiverNickname)) {
            battle.getModel().getStatistics().get(causerNickname).increaseCausedTrackBreaks();
        }
    }

    public static void increaseDirectHits(String receiverNickname, String causerNickname, BattleCalculations battle) {
        if (receiverNickname != null) {
            battle.getModel().getStatistics().get(receiverNickname).increaseReceivedDirectHits();
        }
        if (causerNickname != null && battle.allowedTarget(causerNickname, receiverNickname)) {
            battle.getModel().getStatistics().get(causerNickname).increaseCausedDirectHits();
        }
    }

    public static void increaseIndirectHits(String receiverNickname, String causerNickname, BattleCalculations battle) {
        if (receiverNickname != null) {
            battle.getModel().getStatistics().get(receiverNickname).increaseReceivedIndirectHits();
        }
        if (causerNickname != null) {
            battle.getModel().getStatistics().get(causerNickname).increaseCausedIndirectHits();
        }
    }
}
