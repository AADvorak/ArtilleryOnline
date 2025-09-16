package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.calculator.BodyAccelerationCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.BodyVelocityCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.box.GroundFrictionForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.box.GroundReactionForceCalculator;
import com.github.aadvorak.artilleryonline.battle.calculator.common.GravityForceCalculator;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.common.lines.BodyPart;
import com.github.aadvorak.artilleryonline.battle.config.BoxConfig;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.BoxModel;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.precalc.BoxPreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.BoxSpecs;
import com.github.aadvorak.artilleryonline.battle.state.BoxState;
import com.github.aadvorak.artilleryonline.battle.utils.GroundContactUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class BoxCalculations extends CalculationsBase
        implements BodyCalculations<BoxSpecs, BoxPreCalc, BoxConfig, BoxState, BoxModel> {

    private static final List<CommonForceCalculator> commonForceCalculators = List.of(
            new GravityForceCalculator()
    );

    private static final List<
            ForceCalculator<BoxSpecs, BoxPreCalc, BoxConfig, BoxState, BoxModel, BoxCalculations>
            > forceCalculators = List.of(
            new GroundFrictionForceCalculator(),
            new GroundReactionForceCalculator()
    );

    private static final BodyAccelerationCalculator<
            BoxSpecs, BoxPreCalc, BoxConfig, BoxState, BoxModel, BoxCalculations
            > accelerationCalculator = new BodyAccelerationCalculator<>(commonForceCalculators, forceCalculators);

    private static final BodyVelocityCalculator<
            BoxSpecs, BoxPreCalc, BoxConfig, BoxState, BoxModel, BoxCalculations
            > velocityCalculator = new BodyVelocityCalculator<>(accelerationCalculator);

    private final BoxModel model;

    private Set<Contact> groundContacts;

    private final Next next = new Next();

    @Override
    public Integer getId() {
        return model.getId();
    }

    @Override
    public BoxModel getModel() {
        return model;
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
    public void recalculateVelocity(BattleModel battleModel) {
        velocityCalculator.recalculateVelocity(this, battleModel);
    }

    public void calculateNextPosition(double timeStep) {
        var position = model.getState().getPosition();
        var velocity = model.getState().getVelocity();
        next.setPosition(position.next(velocity, timeStep));
    }

    public void applyNextPosition() {
        model.getState().setPosition(next.getPosition());
    }

    public void applyNormalMoveToNextPosition(double normalMove, double angle) {
        var move = new VectorProjections(angle).setNormal(normalMove).recoverPosition();
        var nextPosition = next.getPosition();
        nextPosition.setX(nextPosition.getX() + move.getX());
        nextPosition.setY(nextPosition.getY() + move.getY());
    }

    public void calculateAllGroundContacts(RoomModel roomModel) {
        var bodyPosition = BodyPosition.of(getGeometryPosition(), model.getState().getPosition().getAngle());
        var bodyPart = BodyPart.of(bodyPosition, model.getSpecs().getShape());
        setGroundContacts(GroundContactUtils.getGroundContacts(bodyPart, roomModel, false));
    }

    @Override
    public BodyPosition getNextPosition() {
        return next.getPosition();
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class Next {
        private BodyPosition position;
    }
}
