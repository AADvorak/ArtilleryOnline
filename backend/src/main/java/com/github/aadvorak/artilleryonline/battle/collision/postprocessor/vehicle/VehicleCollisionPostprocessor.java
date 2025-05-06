package com.github.aadvorak.artilleryonline.battle.collision.postprocessor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.postprocessor.CollisionPostprocessor;
import com.github.aadvorak.artilleryonline.battle.events.VehicleCollideEvent;
import com.github.aadvorak.artilleryonline.dto.response.CollisionResponse;
import org.springframework.stereotype.Component;

@Component
public class VehicleCollisionPostprocessor implements CollisionPostprocessor {

    @Override
    public void process(Calculations<?> calculations, BattleCalculations battle) {
        if (calculations instanceof VehicleCalculations vehicle) {
            if (!vehicle.getCollisions().isEmpty() && !vehicle.isHasCollisions()) {
                vehicle.getCollisions().stream()
                        .filter(collision -> collision.getClosingVelocity() > 1.0)
                        .forEach(collision -> battle.getModel().getEvents().addCollide(
                                new VehicleCollideEvent()
                                        .setVehicleId(vehicle.getModel().getId())
                                        .setObject(CollisionResponse.of(collision)))
                        );
            }
        }
    }
}
