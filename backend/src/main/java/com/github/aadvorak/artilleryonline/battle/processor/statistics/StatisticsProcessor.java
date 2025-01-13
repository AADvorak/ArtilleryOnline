package com.github.aadvorak.artilleryonline.battle.processor.statistics;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

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

    public static void increaseTrackBreaks(VehicleModel vehicleModel, ShellModel shellModel, BattleModel battleModel) {
        if (vehicleModel.getUserId() != null) {
            battleModel.getStatistics().get(vehicleModel.getUserId()).increaseReceivedTrackBreaks();
        }
        if (shellModel.getUserId() != null) {
            battleModel.getStatistics().get(shellModel.getUserId()).increaseCausedTrackBreaks();
        }
    }

    public static void increaseDirectHits(VehicleModel vehicleModel, ShellModel shellModel, BattleModel battleModel) {
        if (vehicleModel.getUserId() != null) {
            battleModel.getStatistics().get(vehicleModel.getUserId()).increaseReceivedDirectHits();
        }
        if (shellModel.getUserId() != null) {
            battleModel.getStatistics().get(shellModel.getUserId()).increaseCausedDirectHits();
        }
    }

    public static void increaseIndirectHits(VehicleModel vehicleModel, ShellModel shellModel, BattleModel battleModel) {
        if (vehicleModel.getUserId() != null) {
            battleModel.getStatistics().get(vehicleModel.getUserId()).increaseReceivedIndirectHits();
        }
        if (shellModel.getUserId() != null) {
            battleModel.getStatistics().get(shellModel.getUserId()).increaseCausedIndirectHits();
        }
    }
}
