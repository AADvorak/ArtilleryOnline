package com.github.aadvorak.artilleryonline.battle.collision.resolver;

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

    private ComponentData normalData;

    private ComponentData tangentialData;

    @Override
    public String toString() {
        return String.format("BodyCollisionData [velocity = %s, velocityProjections = %s, " +
                "normalData = %s, tangentialData = %s]",
                velocity, velocityProjections, normalData, tangentialData);
    }

    public static BodyCollisionData of(BodyModel<?, ?, ?, ?> bodyModel, Contact contact) {
        var comPosition = bodyModel.getState().getPosition().getCenter();
        var radiusNormalized = comPosition.vectorTo(contact.position()).normalized();
        var velocityAtPosition = bodyModel.getState().getVelocityAt(contact.position());
        var tangential = Vector.tangential(contact.angle());
        return new BodyCollisionData()
                .setVelocity(velocityAtPosition)
                .setVelocityProjections(velocityAtPosition.projections(contact.angle()))
                .setNormalData(getComponentData(bodyModel, contact.position(), comPosition, contact.normal(), radiusNormalized))
                .setTangentialData(getComponentData(bodyModel, contact.position(), comPosition, tangential, radiusNormalized));
    }

    private static ComponentData getComponentData(BodyModel<?, ?, ?, ?> bodyModel,
                                                  Position contactPosition, Position comPosition,
                                                  Vector component, Vector radiusNormalized) {
        var radiusAndComponentVectorProduct = radiusNormalized.vectorProduct(component);
        var distanceToAxis = getDistanceToAxis(comPosition, contactPosition, component);
        var inertiaToMassCoefficient = getInertiaToMassCoefficient(radiusAndComponentVectorProduct,
                distanceToAxis, bodyModel.getPreCalc().getMaxRadius());
        return new ComponentData()
                .setDistanceToAxis(distanceToAxis)
                .setInertiaToMassCoefficient(inertiaToMassCoefficient)
                .setRotationSign((byte) Math.signum(radiusAndComponentVectorProduct))
                .setResultMass(getResultMass(bodyModel, distanceToAxis, inertiaToMassCoefficient));
    }

    private static double getInertiaToMassCoefficient(double vectorProduct, double distanceToAxis, double maxRadius) {
        var distanceCoefficient = 0.0;
        if (distanceToAxis > maxRadius) {
            distanceCoefficient = 1.0;
        } else {
            distanceCoefficient = Math.sqrt(distanceToAxis / maxRadius);
        }
        return Math.abs(vectorProduct) * distanceCoefficient;
    }

    private static double getDistanceToAxis(Position comPosition, Position contactPosition, Vector component) {
        var axis = new Segment(contactPosition, contactPosition.shifted(component));
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

    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class ComponentData {

        private double inertiaToMassCoefficient;

        private byte rotationSign;

        private double distanceToAxis;

        private double resultMass;

        @Override
        public String toString() {
            return String.format("[inertiaToMassCoefficient = %.3f, rotationSign = %d," +
                            " distanceToAxis = %.3f, resultMass = %.3f]",
                    inertiaToMassCoefficient, rotationSign, distanceToAxis, resultMass);
        }
    }
}
