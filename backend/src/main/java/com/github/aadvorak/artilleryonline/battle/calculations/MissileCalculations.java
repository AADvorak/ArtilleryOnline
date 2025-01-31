package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.model.MissileModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class MissileCalculations implements Calculations<MissileModel> {

    private final MissileModel model;

    private final Set<Collision> collisions = new HashSet<>();

    private final Next next;

    private final Positions positions;

    public MissileCalculations(MissileModel model) {
        this.model = model;
        positions = new Positions(model.getSpecs().getLength());
        positions.setPosition(model.getState().getPosition());
        next = new Next(new Positions(model.getSpecs().getLength()));
    }

    @Override
    public Integer getId() {
        return model.getId();
    }

    @Override
    public MissileModel getModel() {
        return model;
    }

    @Override
    public VehicleCalculations getVehicleCalculations() {
        return null;
    }

    @Override
    public Position getPosition() {
        return model.getState().getPosition().getCenter();
    }

    @Override
    public Velocity getVelocity() {
        return model.getState().getVelocity().getMovingVelocity();
    }

    @Override
    public double getMass() {
        return model.getSpecs().getMass();
    }

    @Override
    public void setVelocity(Velocity velocity) {
        model.getState().getVelocity()
                .setX(velocity.getX())
                .setY(velocity.getY());
    }

    public void calculateNextPosition(double timeStep) {
        var position = model.getState().getPosition();
        var velocity = model.getState().getVelocity();
        next.getPositions().setPosition(position.next(velocity, timeStep));
    }

    public void applyNextPosition() {
        model.getState().setPosition(next.getPositions().getPosition());
    }

    @Getter
    @RequiredArgsConstructor
    public static final class Next {
        private final Positions positions;
    }

    @RequiredArgsConstructor
    public static final class Positions {

        private final double length;

        @Getter
        private BodyPosition position;

        private Position head;

        private Position tail;

        public Position getHead() {
            if (head == null) {
                head = position.getCenter().shifted(length / 2, position.getAngle());
            }
            return head;
        }

        public Position getTail() {
            if (tail == null) {
                tail = position.getCenter().shifted(- length / 2, position.getAngle());
            }
            return tail;
        }

        public Position getCenter() {
            return position.getCenter();
        }

        public void setPosition(BodyPosition position) {
            this.position = position;
            this.head = null;
            this.tail = null;
        }
    }
}
