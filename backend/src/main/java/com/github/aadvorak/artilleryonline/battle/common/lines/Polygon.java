package com.github.aadvorak.artilleryonline.battle.common.lines;

import com.github.aadvorak.artilleryonline.battle.common.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Set<Position> vertices() {
        return sidesMap.keySet().stream()
                .map(Segment::begin)
                .collect(Collectors.toSet());
    }

    public Segment next(Segment segment) {
        return sidesMap.get(segment);
    }
}
