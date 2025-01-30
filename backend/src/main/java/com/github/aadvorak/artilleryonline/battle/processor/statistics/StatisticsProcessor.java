package com.github.aadvorak.artilleryonline.battle.processor.statistics;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

public class StatisticsProcessor {

    public static void increaseDamage(double damage, Long receiverId,
                                      Long causerId, BattleModel battleModel) {
        if (receiverId != null) {
            battleModel.getStatistics().get(receiverId).increaseReceivedDamage(damage);
        }
        if (causerId != null) {
            battleModel.getStatistics().get(causerId).increaseCausedDamage(damage);
        }
    }

    public static void increaseTrackBreaks(Long receiverId, Long causerId, BattleModel battleModel) {
        if (receiverId != null) {
            battleModel.getStatistics().get(receiverId).increaseReceivedTrackBreaks();
        }
        if (causerId != null) {
            battleModel.getStatistics().get(causerId).increaseCausedTrackBreaks();
        }
    }

    public static void increaseDirectHits(Long receiverId, Long causerId, BattleModel battleModel) {
        if (receiverId != null) {
            battleModel.getStatistics().get(receiverId).increaseReceivedDirectHits();
        }
        if (causerId != null) {
            battleModel.getStatistics().get(causerId).increaseCausedDirectHits();
        }
    }

    public static void increaseIndirectHits(Long receiverId, Long causerId, BattleModel battleModel) {
        if (receiverId != null) {
            battleModel.getStatistics().get(receiverId).increaseReceivedIndirectHits();
        }
        if (causerId != null) {
            battleModel.getStatistics().get(causerId).increaseCausedIndirectHits();
        }
    }
}
