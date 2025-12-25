package com.github.aadvorak.artilleryonline.battle.contact;

import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.lines.Polygon;

public interface PolygonsContactDetector {
    Contact detect(Polygon p1, Polygon p2);
}
