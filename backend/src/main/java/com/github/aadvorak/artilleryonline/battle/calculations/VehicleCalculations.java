package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.calculator.BodyAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.BodyVelocityCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.common.GravityForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.vehicle.GroundFrictionForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.vehicle.GroundReactionForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.vehicle.JetForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.vehicle.EngineForceCalculator;
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
import com.github.aadvorak.artilleryonline.battle.utils.SurfaceContactUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class VehicleCalculations extends CalculationsBase
        implements BodyCalculations<VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel> {

    private static final List<CommonForceCalculator> commonForceCalculators = List.of(
            new GravityForceCalculator()
    );

    private static final List<
            ForceCalculator<VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel, VehicleCalculations>
            > forceCalculators = List.of(
            new GroundFrictionForceCalculator(),
            new GroundReactionForceCalculator(),
            new JetForceCalculator(),
            new EngineForceCalculator()
    );

    private static final BodyAccelerationCalculator<
            VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel, VehicleCalculations
            > accelerationCalculator = new BodyAccelerationCalculator<>(commonForceCalculators, forceCalculators);

    private static final BodyVelocityCalculator<
                VehicleSpecs, VehiclePreCalc, VehicleConfig, VehicleState, VehicleModel, VehicleCalculations
                > velocityCalculator = new BodyVelocityCalculator<>(accelerationCalculator);

    private final VehicleModel model;

    private final WheelCalculations rightWheel;

    private final WheelCalculations leftWheel;

    private Set<Contact> groundContacts;

    private BodyPosition nextPosition;

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
        velocityCalculator.recalculateVelocity(this, battleModel);
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

    public Set<Contact> getGroundContacts() {
        var allGroundContacts = new HashSet<>(groundContacts);
        if (rightWheel.getGroundContact() != null) {
            allGroundContacts.add(rightWheel.getGroundContact());
        }
        if (leftWheel.getGroundContact() != null) {
            allGroundContacts.add(leftWheel.getGroundContact());
        }
        return allGroundContacts;
    }

    public Set<Contact> getTurretGroundContacts() {
        return groundContacts;
    }

    public void calculateAllGroundContacts(RoomModel roomModel) {
        var bodyPosition = BodyPosition.of(getGeometryPosition(), model.getState().getPosition().getAngle());
        var bodyPart = BodyPart.of(bodyPosition, model.getSpecs().getTurretShape());
        var contacts = new HashSet<>(GroundContactUtils.getGroundContacts(bodyPart, roomModel, false));
        contacts.addAll(
                SurfaceContactUtils.getContacts(bodyPart, roomModel, false).stream()
                        .filter(cnt -> cnt.normal().getY() < 0)
                        .collect(Collectors.toSet())
        );
        setGroundContacts(contacts);
        calculateWheelGroundContact(leftWheel, roomModel);
        calculateWheelGroundContact(rightWheel, roomModel);
    }

    private void calculateWheelGroundContact(WheelCalculations wheel, RoomModel roomModel) {
        var wheelRadius = model.getSpecs().getWheelRadius();
        var circle = new Circle(wheel.getPosition(), wheelRadius);
        var contact = GroundContactUtils.getGroundContact(circle, roomModel, false);
        if (contact == null) {
            contact = SurfaceContactUtils.getContacts(circle, roomModel, false).stream()
                    .filter(cnt -> cnt.normal().getY() < 0)
                    .sorted(Comparator.comparingDouble(Contact::depth))
                    .findAny().orElse(null);
        }
        wheel.setGroundContact(contact);
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
