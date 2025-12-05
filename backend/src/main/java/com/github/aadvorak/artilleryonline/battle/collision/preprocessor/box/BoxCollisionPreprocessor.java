package com.github.aadvorak.artilleryonline.battle.collision.preprocessor.box;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.preprocessor.CollisionPreprocessor;
import com.github.aadvorak.artilleryonline.battle.common.BoxType;
import com.github.aadvorak.artilleryonline.battle.events.RepairEvent;
import com.github.aadvorak.artilleryonline.battle.events.RepairEventType;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class BoxCollisionPreprocessor implements CollisionPreprocessor {

    private final ApplicationSettings applicationSettings;

    @Override
    public Boolean process(Collision collision, BattleCalculations battle) {
        var first = collision.getPair().first();
        if (first instanceof BoxCalculations box) {
            return process(box, collision, battle);
        }
        return null;
    }

    private boolean process(BoxCalculations box, Collision collision, BattleCalculations battle) {
        var second = collision.getPair().second();
        if (second instanceof VehicleCalculations vehicle) {
            return !pickBox(box, vehicle, battle);
        }
        if (second instanceof WheelCalculations wheel) {
            return !pickBox(box, wheel.getVehicle(), battle);
        }
        if (!applicationSettings.isClientCollisionsProcessing() ||
                !CollideObjectType.GROUND.equals(collision.getType())
                        && !CollideObjectType.WALL.equals(collision.getType())) {
            box.getModel().getUpdate().setUpdated();
        }
        return true;
    }

    private boolean pickBox(BoxCalculations box, VehicleCalculations vehicle, BattleCalculations battle) {
        RepairEventType eventType = null;
        var hp = vehicle.getModel().getState().getHitPoints();
        var maxHp = vehicle.getModel().getSpecs().getHitPoints();

        if (BoxType.HP.equals(box.getModel().getSpecs().getType()) && hp < maxHp) {
            hp = Math.min(hp + box.getModel().getConfig().getAmount(), maxHp);
            vehicle.getModel().getState().setHitPoints(hp);
            eventType = RepairEventType.HEAL;
        } else if (BoxType.AMMO.equals(box.getModel().getSpecs().getType())
                && vehicle.getModel().getRelativeAmmo() < 1.0) {
            vehicle.getModel().getState().setAmmo(new HashMap<>(vehicle.getModel().getConfig().getAmmo()));
            eventType = RepairEventType.REFILL_AMMO;
        }

        if (eventType != null) {
            battle.getModel().getUpdates().removeBox(box.getId());
            vehicle.getModel().getUpdate().setUpdated();
            battle.getModel().getEvents().addRepair(
                    new RepairEvent()
                            .setVehicleId(vehicle.getModel().getId())
                            .setType(eventType)
            );
            return true;
        }
        return false;
    }
}
