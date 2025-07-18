package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.calculator.BodyAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.vehicle.*;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.common.lines.BodyPart;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.config.VehicleConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.precalc.VehiclePreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.battle.state.VehicleState;
import com.github.aadvorak.artilleryonline.battle.utils.GroundContactUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class VehicleCalculations
        implements BodyCalculations<VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel> {

    private static final List<
            ForceCalculator<VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel, VehicleCalculations>
            > forceCalculators = List.of(
            new GravityForceCalculator(),
            new GroundFrictionForceCalculator(),
            new GroundReactionForceCalculator(),
            new JetForceCalculator(),
            new EngineForceCalculator()
    );

    private static final BodyAccelerationCalculator<
            VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel, VehicleCalculations
            > calculator = new BodyAccelerationCalculator<>(forceCalculators);

    private final VehicleModel model;

    private final WheelCalculations rightWheel;

    private final WheelCalculations leftWheel;

    private Set<Contact> groundContacts;

    private BodyPosition nextPosition;

    private Set<Collision> collisions = new HashSet<>();

    private boolean hasCollisions = false;

    public VehicleCalculations(VehicleModel model) {
        this.model = model;
        rightWheel = new WheelCalculations(WheelSign.RIGHT, this);
        leftWheel = new WheelCalculations(WheelSign.LEFT, this);
        recalculateWheelsVelocities();
    }

    @Override
    public Integer getId() {
        return model.getId();
    }

    @Override
    public Position getPosition() {
        return model.getState().getPosition().getCenter();
    }

    public Position getGeometryPosition() {
        return getPosition().shifted(model.getPreCalc().getCenterOfMassShift()
                .plusAngle(model.getState().getPosition().getAngle()).inverted());
    }

    public BodyPosition getGeometryNextPosition() {
        return nextPosition.shifted(model.getPreCalc().getCenterOfMassShift()
                .plusAngle(nextPosition.getAngle()).inverted());
    }

    @Override
    public Velocity getVelocity() {
        return model.getState().getVelocity().getMovingVelocity();
    }

    @Override
    public double getMass() {
        return model.getPreCalc().getMass();
    }

    @Override
    public void recalculateVelocity(BattleModel battleModel) {
        calculateAllGroundContacts(battleModel.getRoom());
        var acceleration = calculator.calculate(this, battleModel);
        var vehicleVelocity = model.getState().getVelocity();
        var timeStep = battleModel.getCurrentTimeStepSecs();
        var maxRadius = model.getPreCalc().getMaxRadius();
        vehicleVelocity.recalculate(acceleration, timeStep);
        if (Math.abs(vehicleVelocity.getX()) < Constants.MIN_VELOCITY) {
            vehicleVelocity.setX(0.0);
        }
        if (Math.abs(vehicleVelocity.getY()) < Constants.MIN_VELOCITY) {
            vehicleVelocity.setY(0.0);
        }
        if (Math.abs(vehicleVelocity.getAngle() * maxRadius) < Constants.MIN_VELOCITY) {
            vehicleVelocity.setAngle(0.0);
        }
        recalculateWheelsVelocities();
    }

    public void recalculateWheelsVelocities() {
        rightWheel.recalculateVelocity();
        leftWheel.recalculateVelocity();
    }

    public void applyNextPosition() {
        model.getState().setPosition(nextPosition);
        calculateOnGround();
    }

    public void applyNormalMoveToNextPosition(double normalMove, double angle) {
        var move = new VectorProjections(angle).setNormal(normalMove).recoverPosition();
        nextPosition.setX(nextPosition.getX() + move.getX());
        nextPosition.setY(nextPosition.getY() + move.getY());
    }

    public void calculateNextPosition(double timeStep) {
        var position = model.getState().getPosition();
        var velocity = model.getState().getVelocity();
        setNextPosition(position.next(velocity, timeStep));
        rightWheel.calculateNextPosition();
        leftWheel.calculateNextPosition();
    }

    public Set<Contact> getAllGroundContacts() {
        var allGroundContacts = new HashSet<>(groundContacts);
        if (rightWheel.getGroundContact() != null) {
            allGroundContacts.add(rightWheel.getGroundContact());
        }
        if (leftWheel.getGroundContact() != null) {
            allGroundContacts.add(leftWheel.getGroundContact());
        }
        return allGroundContacts;
    }

    public void calculateAllGroundContacts(RoomModel roomModel) {
        var bodyPosition = BodyPosition.of(getGeometryPosition(), model.getState().getPosition().getAngle());
        var bodyPart = BodyPart.of(bodyPosition, model.getSpecs().getTurretShape());
        setGroundContacts(GroundContactUtils.getGroundContacts(bodyPart, roomModel, false));
        var wheelRadius = model.getSpecs().getWheelRadius();
        rightWheel.setGroundContact(GroundContactUtils.getGroundContact(
                new Circle(rightWheel.getPosition(), wheelRadius),
                roomModel, false));
        leftWheel.setGroundContact(GroundContactUtils.getGroundContact(
                new Circle(leftWheel.getPosition(), wheelRadius),
                roomModel, false));
    }

    private void calculateOnGround() {
        var state = model.getState();
        var onGround = rightWheel.getGroundContact() != null
                || leftWheel.getGroundContact() != null;
        if (state.isOnGround() != onGround) {
            state.setOnGround(onGround);
            model.getUpdate().setUpdated();
        }
    }
}
