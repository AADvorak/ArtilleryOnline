package com.github.aadvorak.artilleryonline.battle.state;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.Boundaries;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;
import com.github.aadvorak.artilleryonline.battle.common.lines.Trapeze;
import com.github.aadvorak.artilleryonline.battle.common.shapes.TrapezeShape;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SurfaceState implements State, CompactSerializable {

    private Position begin;

    private Position end;

    private double width;

    private Trapeze trapeze;

    private Boundaries boundaries;

    public Trapeze getTrapeze() {
        if (trapeze == null) {
            var segment = new Segment(begin, end);
            var geometryPosition = BodyPosition.of(
                    segment.center().shifted(segment.normal().multiply(-width / 2)),
                    begin.angleTo(end)
            );
            // yes, the width of the surface is the height of rectangle
            trapeze = new Trapeze(geometryPosition, TrapezeShape.rectangle(begin.distanceTo(end), width));
        }
        return trapeze;
    }

    public Boundaries getBoundaries() {
        if (boundaries == null) {
            var segment = new Segment(begin, end);
            var sb = segment.boundaries();
            boundaries = new Boundaries(
                    sb.xMin() - width / 2, sb.xMax() + width / 2,
                    sb.yMin() - width / 2, sb.yMax() + width / 2
            );
        }
        return boundaries;
    }

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeSerializableValue(begin);
        stream.writeSerializableValue(end);
        stream.writeDouble(width);
    }
}
