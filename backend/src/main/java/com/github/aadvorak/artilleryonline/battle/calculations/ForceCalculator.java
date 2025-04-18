package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.config.Config;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.BodyModel;
import com.github.aadvorak.artilleryonline.battle.precalc.BodyPreCalc;
import com.github.aadvorak.artilleryonline.battle.specs.Specs;
import com.github.aadvorak.artilleryonline.battle.state.BodyState;

import java.util.List;

public interface ForceCalculator<
        S extends Specs,
        P extends BodyPreCalc,
        Cf extends Config,
        St extends BodyState,
        M extends BodyModel<S, P, Cf, St>,
        C extends BodyCalculations<S, P, Cf, St, M>> {
    List<ForceAtPoint> calculate(C calculations, BattleModel battleModel);
}
