package com.github.aadvorak.artilleryonline.battle.collision.preprocessor.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.preprocessor.CollisionPreprocessor;
import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.events.RicochetEvent;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.processor.bomber.BomberFlyProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.statistics.StatisticsProcessor;
import com.github.aadvorak.artilleryonline.dto.response.ContactResponse;
import org.springframework.stereotype.Component;

@Component
public class ShellCollisionPreprocessor implements CollisionPreprocessor {

    @Override
    public Boolean process(Collision collision, BattleCalculations battle) {
        var first = collision.getPair().first();
        if (first instanceof ShellCalculations shell) {
            return process(shell, collision, battle);
        }
        return null;
    }

    private boolean process(ShellCalculations shell, Collision collision, BattleCalculations battle) {
        if (shell.getModel().getState().isStuck()) {
            return false;
        }
        shell.getNext().setPosition(collision.getContact().position());
        return switch (collision.getType()) {
            case DRONE -> processDrone(shell, collision, battle);
            case VEHICLE -> processVehicle(shell, collision, battle);
            case MISSILE -> processMissile(shell, collision, battle);
            case BOX -> processBox(shell, collision, battle);
            case GROUND -> processGround(shell, collision, battle);
            case SURFACE -> processSurface(shell);
            default -> false;
        };
    }

    private boolean processDrone(ShellCalculations shell, Collision collision, BattleCalculations battle) {
        var drone = (DroneCalculations) collision.getPair().second();
        drone.getModel().getUpdate().setUpdated();
        if (!ShellType.SGN.equals(shell.getModel().getSpecs().getType())) {
            collision.setHit(true);
            drone.getModel().getState().setDestroyed(true);
            StatisticsProcessor.increaseDestroyedDrones(shell.getModel().getUserId(), battle.getModel());
            DamageProcessor.processHitDrone(drone, shell, battle);
        }
        return true;
    }

    private boolean processVehicle(ShellCalculations shell, Collision collision, BattleCalculations battle) {
        var hitObject = collision.getPair().second();
        ((VehicleModel) hitObject.getModel()).getUpdate().setUpdated();
        if (collision.isRicochet()) {
            battle.getModel().getEvents().addRicochet(
                    new RicochetEvent()
                            .setShellId(shell.getId())
                            .setContact(ContactResponse.of(collision.getContact()))
            );
        } else if (!ShellType.SGN.equals(shell.getModel().getSpecs().getType())) {
            collision.setHit(true);
            if (hitObject instanceof VehicleCalculations vehicle) {
                DamageProcessor.processHitVehicle(vehicle, shell, battle);
            }
            if (hitObject instanceof WheelCalculations wheel) {
                DamageProcessor.processHitTrack(wheel.getVehicle(), shell, battle);
            }
        }
        return true;
    }

    private boolean processMissile(ShellCalculations shell, Collision collision, BattleCalculations battle) {
        var missile = (MissileCalculations) collision.getPair().second();
        DamageProcessor.processHit(missile, battle);
        DamageProcessor.processHit(shell, battle);
        battle.getModel().getUpdates().removeMissile(missile.getId());
        StatisticsProcessor.increaseDestroyedMissiles(shell.getModel().getUserId(), battle.getModel());
        return false;
    }

    private boolean processBox(ShellCalculations shell, Collision collision, BattleCalculations battle) {
        if (!ShellType.SGN.equals(shell.getModel().getSpecs().getType())) {
            collision.setHit(true);
            var box = (BoxCalculations) collision.getPair().second();
            DamageProcessor.processHit(shell, battle);
            battle.getModel().getUpdates().removeBox(box.getId());
        }
        return false;
    }

    private boolean processGround(ShellCalculations shell, Collision collision, BattleCalculations battle) {
        if (ShellType.SGN.equals(shell.getModel().getSpecs().getType())) {
            shell.getModel().getState().setStuck(true);
            shell.getModel().getState().setVelocity(new Velocity());
            shell.applyNextPosition();
            shell.getModel().setUpdated(true);
            BomberFlyProcessor.fly(shell.getPosition(), shell.getModel().getVehicleId(), battle.getModel());
        } else {
            collision.setHit(true);
            DamageProcessor.processHit(shell, battle);
        }
        return false;
    }

    private boolean processSurface(ShellCalculations shell) {
        shell.getModel().setUpdated(true);
        return true;
    }
}
