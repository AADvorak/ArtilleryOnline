package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.config.Config;
import com.github.aadvorak.artilleryonline.battle.model.BodyModel;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.precalc.BodyPreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.Specs;
import com.github.aadvorak.artilleryonline.battle.state.BodyState;

public interface BodyCalculations<
        S extends Specs,
        P extends BodyPreCalc,
        C extends Config,
        St extends BodyState,
        M extends BodyModel<S, P, C, St>
        > extends Calculations<M> {

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
}
