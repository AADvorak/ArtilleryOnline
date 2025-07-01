package com.github.aadvorak.artilleryonline.battle.collision.detector.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.BodyPart;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.utils.GroundContactUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class VehicleGroundCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof VehicleCalculations vehicleCalculations) {
            return detect(vehicleCalculations, battle, first);
        }
        return Set.of();
    }

    private Set<Collision> detect(VehicleCalculations vehicle, BattleCalculations battle, boolean first) {
        Set<Collision> collisions = new HashSet<>();
        for (var wheel : List.of(vehicle.getRightWheel(), vehicle.getLeftWheel())) {
            var groundCollision = detectWheelGroundCollision(wheel, battle);
            if (groundCollision != null) {
                collisions.add(groundCollision);
                if (first) return collisions;
            }
            var wallCollision = detectWheelWallCollision(wheel, battle);
            if (wallCollision != null) {
                collisions.add(wallCollision);
                if (first) return collisions;
            }
        }
        collisions.addAll(detectHullGroundCollisions(vehicle, battle));
        return collisions;
    }

    private Collision detectWheelGroundCollision(WheelCalculations wheel, BattleCalculations battle) {
        var contact = GroundContactUtils.getGroundContact(
                new Circle(wheel.getNext().getPosition(), wheel.getModel().getSpecs().getWheelRadius()),
                battle.getModel().getRoom(), true);
        if (contact == null) {
            return null;
        }
        return Collision.withGround(wheel, contact);
    }

    private Set<Collision> detectHullGroundCollisions(VehicleCalculations vehicle, BattleCalculations battle) {
        var bodyPosition = BodyPosition.of(vehicle.getGeometryPosition(),
                vehicle.getModel().getState().getPosition().getAngle());
        var bodyPart = BodyPart.of(bodyPosition, vehicle.getModel().getSpecs().getTurretShape());
        return GroundContactUtils.getGroundContacts(bodyPart,
                        battle.getModel().getRoom(), true).stream()
                .map(contact -> Collision.withGround(vehicle, contact))
                .collect(Collectors.toSet());
    }

    private Collision detectWheelWallCollision(WheelCalculations wheel, BattleCalculations battle) {
        var wheelRadius = wheel.getVehicle().getModel().getSpecs().getWheelRadius();
        var xMax = battle.getModel().getRoom().getSpecs().getRightTop().getX();
        var xMin = battle.getModel().getRoom().getSpecs().getLeftBottom().getX();
        var nextPosition = wheel.getNext().getPosition();
        var rightWallDepth = nextPosition.getX() + wheelRadius - xMax;
        var rightWallContact = Contact.of(rightWallDepth, Math.PI / 2,
                new Position().setX(xMax).setY(nextPosition.getY()));
        if (rightWallContact != null) {
            return Collision.withWall(wheel, rightWallContact);
        }
        var leftWallDepth = xMin - nextPosition.getX() + wheelRadius;
        var leftWallContact = Contact.of(leftWallDepth, -Math.PI / 2,
                new Position().setX(xMin).setY(nextPosition.getY()));
        if (leftWallContact != null) {
            return Collision.withWall(wheel, leftWallContact);
        }
        return null;
    }
}
