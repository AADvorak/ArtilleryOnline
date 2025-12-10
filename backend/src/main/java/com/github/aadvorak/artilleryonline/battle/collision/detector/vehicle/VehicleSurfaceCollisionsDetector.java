package com.github.aadvorak.artilleryonline.battle.collision.detector.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.lines.BodyPart;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.utils.SurfaceContactUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class VehicleSurfaceCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof VehicleCalculations vehicleCalculations) {
            return detect(vehicleCalculations, battle);
        }
        return Set.of();
    }

    private Set<Collision> detect(VehicleCalculations vehicle, BattleCalculations battle) {
        Set<Collision> collisions = new HashSet<>();
        for (var wheel : List.of(vehicle.getRightWheel(), vehicle.getLeftWheel())) {
            collisions.addAll(detectWheelCollisions(wheel, battle));
        }
        collisions.addAll(detectHullCollisions(vehicle, battle));
        return collisions;
    }

    private Set<Collision> detectWheelCollisions(WheelCalculations wheel, BattleCalculations battle) {
        return SurfaceContactUtils.getContacts(
                        new Circle(wheel.getNext().getPosition(), wheel.getModel().getSpecs().getWheelRadius()),
                        battle.getModel().getRoom(), true)
                .stream()
                .map(contact -> Collision.withSurface(wheel, contact))
                .collect(Collectors.toSet());
    }

    private Set<Collision> detectHullCollisions(VehicleCalculations vehicle, BattleCalculations battle) {
        var bodyPart = BodyPart.of(vehicle.getGeometryNextPosition(), vehicle.getModel().getSpecs().getTurretShape());
        return SurfaceContactUtils.getContacts(bodyPart,
                        battle.getModel().getRoom(), true).stream()
                .map(contact -> Collision.withSurface(vehicle, contact))
                .collect(Collectors.toSet());
    }
}
