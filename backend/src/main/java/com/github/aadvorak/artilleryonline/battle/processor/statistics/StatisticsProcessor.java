package com.github.aadvorak.artilleryonline.battle.processor.statistics;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

public class StatisticsProcessor {

    public static void increaseDestroyedDrones(String nickname, BattleModel battleModel) {
        if (nickname != null) {
            battleModel.getStatistics().get(nickname).increaseDestroyedDrones();
        }
    }

    public static void increaseDestroyedMissiles(String nickname, BattleModel battleModel) {
        if (nickname != null) {
            battleModel.getStatistics().get(nickname).increaseDestroyedMissiles();
        }
    }

    public static void increaseDamage(double damage, String receiverNickname,
                                      String causerNickname, BattleModel battleModel) {
        if (receiverNickname != null) {
            battleModel.getStatistics().get(receiverNickname).increaseReceivedDamage(damage);
        }
        if (causerNickname != null) {
            battleModel.getStatistics().get(causerNickname).increaseCausedDamage(damage);
        }
    }

    public static void increaseTrackBreaks(String receiverNickname, String causerNickname, BattleModel battleModel) {
        if (receiverNickname != null) {
            battleModel.getStatistics().get(receiverNickname).increaseReceivedTrackBreaks();
        }
        if (causerNickname != null) {
            battleModel.getStatistics().get(causerNickname).increaseCausedTrackBreaks();
        }
    }

    public static void increaseDirectHits(String receiverNickname, String causerNickname, BattleModel battleModel) {
        if (receiverNickname != null) {
            battleModel.getStatistics().get(receiverNickname).increaseReceivedDirectHits();
        }
        if (causerNickname != null) {
            battleModel.getStatistics().get(causerNickname).increaseCausedDirectHits();
        }
    }

    public static void increaseIndirectHits(String receiverNickname, String causerNickname, BattleModel battleModel) {
        if (receiverNickname != null) {
            battleModel.getStatistics().get(receiverNickname).increaseReceivedIndirectHits();
        }
        if (causerNickname != null) {
            battleModel.getStatistics().get(causerNickname).increaseCausedIndirectHits();
        }
    }
}
