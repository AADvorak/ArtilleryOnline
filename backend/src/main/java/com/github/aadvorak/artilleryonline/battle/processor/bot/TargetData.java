package com.github.aadvorak.artilleryonline.battle.processor.bot;

import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Vector;

public record TargetData(Contact contact, Vector hitNormal, Double armor, Double penetration) {
}
