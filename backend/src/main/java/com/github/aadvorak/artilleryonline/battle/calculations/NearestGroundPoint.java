package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.common.Position;

public record NearestGroundPoint(Position position, Double distance, Integer index) {
}
