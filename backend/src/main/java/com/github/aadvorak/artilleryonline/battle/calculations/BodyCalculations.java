package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.config.Config;
import com.github.aadvorak.artilleryonline.battle.model.BodyModel;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.precalc.BodyPreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.Specs;
import com.github.aadvorak.artilleryonline.battle.state.BodyState;

import java.util.Set;

public interface BodyCalculations<
        S extends Specs,
        P extends BodyPreCalc,
        C extends Config,
        St extends BodyState,
        M extends BodyModel<S, P, C, St>
        > extends Calculations<M> {

    Set<Contact> getGroundContacts();

    void calculateAllGroundContacts(RoomModel roomModel);

    BodyPosition getNextPosition();

    default Position getGeometryPosition() {
        return getPosition().shifted(getModel().getPreCalc().getCenterOfMassShift()
                .plusAngle(getModel().getState().getPosition().getAngle()).inverted());
    }

    default BodyPosition getGeometryNextPosition() {
        return getNextPosition().shifted(getModel().getPreCalc().getCenterOfMassShift()
                .plusAngle(getNextPosition().getAngle()).inverted());
    }

    default double getKineticEnergy() {
        return getMovingKineticEnergy() + getRotatingKineticEnergy();
    }

    default double getMovingKineticEnergy() {
        var velocity = getModel().getState().getVelocity();
        return getMass() * (Math.pow(velocity.getX(), 2) + Math.pow(velocity.getY(), 2)) / 2;
    }

    default double getRotatingKineticEnergy() {
        return getModel().getPreCalc().getMomentOfInertia()
                * Math.pow(getModel().getState().getVelocity().getAngle(), 2) / 2;
    }
}
