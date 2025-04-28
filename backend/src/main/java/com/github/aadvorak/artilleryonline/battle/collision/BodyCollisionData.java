package com.github.aadvorak.artilleryonline.battle.collision;

import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;
import com.github.aadvorak.artilleryonline.battle.model.BodyModel;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BodyCollisionData {

    private Velocity velocity;

    private VectorProjections velocityProjections;

    private double inertiaToMassCoefficient;

    private byte rotationSign;

    private double distanceToAxis;

    private double resultMass;

    @Override
    public String toString() {
        return String.format("BodyCollisionData [velocity = %s, velocityProjections = %s, " +
                "inertiaToMassCoefficient = %.3f, rotationSign = %d, distanceToAxis = %.3f, resultMass = %.3f]",
                velocity, velocityProjections, inertiaToMassCoefficient, rotationSign, distanceToAxis, resultMass);
    }

    public static BodyCollisionData of(BodyModel<?, ?, ?, ?> bodyModel, Contact contact) {
        var velocityAtPosition = getVelocityAtPosition(bodyModel, contact.position());
        var radiusAndNormalVectorProduct = getRadiusAndNormalVectorProduct(bodyModel, contact);
        var inertiaToMassCoefficient = getInertiaToMassCoefficient(radiusAndNormalVectorProduct);
        var distanceToAxis = getDistanceToAxis(bodyModel, contact);
        return new BodyCollisionData()
                .setVelocity(velocityAtPosition)
                .setVelocityProjections(velocityAtPosition.projections(contact.angle()))
                .setInertiaToMassCoefficient(inertiaToMassCoefficient)
                .setRotationSign((byte) -Math.signum(radiusAndNormalVectorProduct))
                .setDistanceToAxis(distanceToAxis)
                .setResultMass(getResultMass(bodyModel, distanceToAxis, inertiaToMassCoefficient));
    }

    private static Velocity getVelocityAtPosition(BodyModel<?, ?, ?, ?> bodyModel, Position position) {
        var bodyVelocity = bodyModel.getState().getVelocity();
        var comPosition = bodyModel.getState().getPosition().getCenter();
        var angle = comPosition.angleTo(position);
        var distance = comPosition.distanceTo(position);
        var rvx = bodyVelocity.getAngle() * distance * Math.sin(angle);
        var rvy = bodyVelocity.getAngle() * distance * Math.cos(angle);
        return new Velocity()
                .setX(bodyVelocity.getX() + rvx)
                .setY(bodyVelocity.getY() + rvy);
    }

    private static double getRadiusAndNormalVectorProduct(BodyModel<?, ?, ?, ?> bodyModel, Contact contact) {
        var comPosition = bodyModel.getState().getPosition().getCenter();
        var radiusNormalized = comPosition.vectorTo(contact.position()).normalized();
        return radiusNormalized.vectorProduct(contact.normal());
    }

    private static double getInertiaToMassCoefficient(double vectorProduct) {
        var inertiaToMassCoefficient = Math.abs(vectorProduct);
        if (inertiaToMassCoefficient < 0.1) {
            return 0;
        }
        if (inertiaToMassCoefficient > 0.9) {
            return 1;
        }
        return inertiaToMassCoefficient;
    }

    private static double getDistanceToAxis(BodyModel<?, ?, ?, ?> bodyModel, Contact contact) {
        var axis = new Segment(contact.position(), contact.position().shifted(contact.normal()));
        var comPosition = bodyModel.getState().getPosition().getCenter();
        var axisProjection = GeometryUtils.getPointToLineProjection(comPosition, axis);
        return comPosition.distanceTo(axisProjection);
    }

    private static double getResultMass(BodyModel<?, ?, ?, ?> bodyModel,
                                        double distanceToAxis, double inertiaToMassCoefficient) {
        var mass = bodyModel.getPreCalc().getMass();
        if (inertiaToMassCoefficient == 0.0) {
            return mass;
        }
        var momentOfInertia = bodyModel.getPreCalc().getMomentOfInertia();
        return inertiaToMassCoefficient * momentOfInertia / Math.pow(distanceToAxis, 2)
                + (1 - inertiaToMassCoefficient) * mass;
    }
}
