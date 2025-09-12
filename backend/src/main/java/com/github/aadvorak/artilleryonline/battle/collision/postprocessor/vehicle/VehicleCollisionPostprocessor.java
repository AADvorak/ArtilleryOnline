package com.github.aadvorak.artilleryonline.battle.collision.postprocessor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.collision.postprocessor.CollisionPostprocessor;
import com.github.aadvorak.artilleryonline.battle.events.CollideEvent;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.dto.response.CollisionResponse;
import org.springframework.stereotype.Component;

@Component
public class VehicleCollisionPostprocessor implements CollisionPostprocessor {

    private static final long SOUND_DELAY = 1000;

    @Override
    public void process(Calculations<?> calculations, BattleCalculations battle) {
        if (calculations instanceof VehicleCalculations vehicle) {
            if (!vehicle.getAllCollisions().isEmpty() && canPlaySound(vehicle.getModel(), battle.getTime())) {
                vehicle.getAllCollisions().stream()
                        .filter(collision -> collision.getClosingVelocity() > 1.0)
                        .forEach(collision -> {
                                    battle.getModel().getEvents().addCollide(
                                            new CollideEvent()
                                                    .setId(vehicle.getModel().getId())
                                                    .setType(CollideObjectType.VEHICLE)
                                                    .setObject(CollisionResponse.of(collision)));
                                    vehicle.getModel().setLastSoundTime(battle.getTime());
                                }
                        );
            }
        }
    }

    private boolean canPlaySound(VehicleModel model, long currentTime) {
        return currentTime - model.getLastSoundTime() > SOUND_DELAY;
    }
}
