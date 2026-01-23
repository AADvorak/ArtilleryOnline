package com.github.aadvorak.artilleryonline.battle.processor.bot;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Vector;
import com.github.aadvorak.artilleryonline.battle.common.VectorImpl;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.TrajectoryContactUtils;

public class TargetDataCalculator {

    public TargetData calculate(VehicleCalculations vehicle, BattleCalculations battle) {
        var selectedShell = vehicle.getModel().getState().getGunState().getSelectedShell();
        if (selectedShell == null) {
            return null;
        }
        var shellSpecs = vehicle.getModel().getConfig().getGun().getAvailableShells().get(selectedShell);
        if (shellSpecs == null) {
            return null;
        }
        var startPosition = getGunEndPosition(vehicle);
        var angle = vehicle.getModel().getState().getPosition().getAngle() + vehicle.getModel().getState().getGunState().getAngle();
        var directionSign = Math.signum(Math.cos(angle));
        var maxX = battle.getModel().getRoom().getSpecs().getRightTop().getX();
        
        var x = startPosition.getX();
        var y = startPosition.getY();
        var previous = new Position().setX(x).setY(y);
        
        var step = 0.2;
        TargetData targetData = null;
        
        while (x > 0 && x < maxX && y > 0) {
            x += step * directionSign;
            double x1 = x - startPosition.getX();
            y = startPosition.getY() + x1 * Math.tan(angle) - battle.getModel().getRoom().getSpecs().getGravityAcceleration() * x1 * x1
                    / (2 * shellSpecs.getVelocity() * shellSpecs.getVelocity() * Math.cos(angle) * Math.cos(angle));
            
            Segment trajectory = new Segment(previous, new Position().setX(x).setY(y));
            Vector hitNormal = new VectorImpl().setX(x - previous.getX()).setY(y - previous.getY()).normalized();

            for (VehicleCalculations otherVehicle : battle.getVehicles()) {
                if (otherVehicle.getId().equals(vehicle.getId())) {
                    continue;
                }
                var bodyContact = TrajectoryContactUtils.detectWithVehicle(trajectory, otherVehicle);
                if (bodyContact != null) {
                    targetData = new TargetData(
                            bodyContact.contact(),
                            hitNormal,
                            vehicle.getId(),
                            shellSpecs.getPenetration(),
                            otherVehicle.getModel().getSpecs().getArmor().get(bodyContact.hitSurface())
                    );
                    break;
                }
                var wheelContact = TrajectoryContactUtils.detectWithWheel(trajectory, otherVehicle.getRightWheel());
                if (wheelContact != null) {
                    targetData = new TargetData(wheelContact, hitNormal, vehicle.getId(), null, null);
                    break;
                }
                wheelContact = TrajectoryContactUtils.detectWithWheel(trajectory, otherVehicle.getLeftWheel());
                if (wheelContact != null) {
                    targetData = new TargetData(wheelContact, hitNormal, vehicle.getId(), null, null);
                    break;
                }
            }

            if (targetData != null) {
                break;
            } else  {
                Position groundPosition = BattleUtils.getGroundIntersectionPoint(trajectory, battle.getModel().getRoom());
                if (groundPosition != null) {
                    var contact = Contact.withUncheckedDepthOf(0, 0, groundPosition);
                    targetData = new TargetData(contact, hitNormal, null, null, null);
                    break;
                }
            }
            previous = new Position().setX(x).setY(y);
        }

        return targetData;
    }

    private Position getGunEndPosition(VehicleCalculations vehicle) {
        var vehiclePosition = vehicle.getModel().getState().getPosition();
        var gunAngle = vehiclePosition.getAngle() + vehicle.getModel().getState().getGunState().getAngle();
        var gunLength = vehicle.getModel().getConfig().getGun().getLength();
        return vehiclePosition.getCenter().shifted(gunLength, gunAngle);
    }
}
