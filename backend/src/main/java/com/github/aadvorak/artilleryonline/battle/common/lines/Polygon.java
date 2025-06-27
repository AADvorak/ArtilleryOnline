package com.github.aadvorak.artilleryonline.battle.common.lines;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Polygon {

    private final Map<Segment, Segment> sidesMap;

    public Polygon(Trapeze trapeze) {
        var bottom = trapeze.bottom();
        var right = trapeze.right();
        var top = trapeze.top();
        var left = trapeze.left();
        sidesMap = new HashMap<>();
        sidesMap.put(bottom, right);
        sidesMap.put(right, top);
        sidesMap.put(top, left);
        sidesMap.put(left, bottom);
    }

    public Set<Segment> sides() {
        return sidesMap.keySet();
    }

    public Segment next(Segment segment) {
        return sidesMap.get(segment);
    }
}
