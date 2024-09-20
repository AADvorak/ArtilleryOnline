package com.github.aadvorak.artilleryonline.battle.processor.shell;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.specs.ShellSpecs;

public class ShellDamageProcessor {

    public static void processHitVehicle(Position hitPosition, VehicleModel vehicleModel,
                                         ShellSpecs shellSpecs, BattleModel battleModel) {
        if (ShellType.AP.equals(shellSpecs.getType())) {
            applyDamageToVehicle(shellSpecs.getDamage(), vehicleModel, battleModel);
        } else if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hitPosition, shellSpecs, battleModel);
        }
    }

    public static void processHitTrack(Position hitPosition, VehicleModel vehicleModel,
                                         ShellSpecs shellSpecs, BattleModel battleModel) {
        var trackState = vehicleModel.getState().getTrackState();
        trackState.setBroken(true);
        trackState.setRepairRemainTime(vehicleModel.getSpecs().getTrackRepairTime());
        if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hitPosition, shellSpecs, battleModel);
        }
    }

    public static void processHitGround(Position hitPosition, ShellSpecs shellSpecs, BattleModel battleModel) {
        if (ShellType.HE.equals(shellSpecs.getType())) {
            calculateHEDamage(hitPosition, shellSpecs, battleModel);
        }
        // todo ground destruction
        // todo possible track destruction
    }

    private static void calculateHEDamage(Position hitPosition, ShellSpecs shellSpecs, BattleModel battleModel) {
        battleModel.getVehicles().values().forEach(vehicleModel -> {
            var distanceToTarget = hitPosition.distanceTo(vehicleModel.getState().getPosition())
                    - vehicleModel.getSpecs().getRadius();
            if (distanceToTarget <= 0) {
                applyDamageToVehicle(shellSpecs.getDamage(), vehicleModel, battleModel);
            } else if (distanceToTarget < shellSpecs.getRadius()) {
                applyDamageToVehicle(shellSpecs.getDamage() * (shellSpecs.getRadius() - distanceToTarget)
                        / shellSpecs.getRadius(), vehicleModel, battleModel);
            }
        });
    }

    private static void applyDamageToVehicle(double damage, VehicleModel vehicleModel, BattleModel battleModel) {
        var hitPoints = vehicleModel.getState().getHitPoints() - damage;
        if (hitPoints <= 0) {
            battleModel.removeVehicleById(vehicleModel.getId());
        } else {
            vehicleModel.getState().setHitPoints(hitPoints);
        }
    }
}
