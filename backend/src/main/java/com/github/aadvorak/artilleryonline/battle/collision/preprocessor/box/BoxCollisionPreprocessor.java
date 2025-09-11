package com.github.aadvorak.artilleryonline.battle.collision.preprocessor.box;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.preprocessor.CollisionPreprocessor;
import com.github.aadvorak.artilleryonline.battle.common.BoxType;
import org.springframework.stereotype.Component;

@Component
public class BoxCollisionPreprocessor implements CollisionPreprocessor {

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
        return true;
    }

    private boolean pickBox(BoxCalculations box, VehicleCalculations vehicle, BattleCalculations battle) {
        var hp = vehicle.getModel().getState().getHitPoints();
        var maxHp = vehicle.getModel().getSpecs().getHitPoints();
        if (BoxType.HP.equals(box.getModel().getSpecs().getType()) && hp < maxHp) {
            battle.getModel().getUpdates().removeBox(box.getId());
            hp = Math.min(hp + box.getModel().getConfig().getAmount(), maxHp);
            vehicle.getModel().getState().setHitPoints(hp);
            vehicle.getModel().getUpdate().setUpdated();
            return true;
        }
        return false;
    }
}
