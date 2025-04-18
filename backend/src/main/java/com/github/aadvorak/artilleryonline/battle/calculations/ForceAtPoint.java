package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Force;
import com.github.aadvorak.artilleryonline.battle.common.Position;

public record ForceAtPoint(Force force, Position point) {

    public static ForceAtPoint atCOM(Force force) {
        return new ForceAtPoint(force, null);
    }

    public static ForceAtPoint zero() {
        return new ForceAtPoint(new Force(), null);
    }
}
