package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Force;
import com.github.aadvorak.artilleryonline.battle.common.Position;

public record ForceAtPoint(Force force, Position point, String description) {

    public static ForceAtPoint atCOM(Force force, String description) {
        return new ForceAtPoint(force, null, description);
    }

    public static ForceAtPoint zero(String description) {
        return new ForceAtPoint(new Force(), null, description);
    }
}
