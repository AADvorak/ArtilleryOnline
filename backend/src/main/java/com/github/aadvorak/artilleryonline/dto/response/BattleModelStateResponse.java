package com.github.aadvorak.artilleryonline.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.aadvorak.artilleryonline.battle.state.*;
import com.github.aadvorak.artilleryonline.serialization.ByteArrayOutputStreamWrapper;
import com.github.aadvorak.artilleryonline.serialization.CompactSerializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleModelStateResponse implements CompactSerializable {

    private Map<String, VehicleState> vehicles;

    private Map<Integer, ShellState> shells;

    private Map<Integer, MissileState> missiles;

    private Map<Integer, DroneState> drones;

    private Map<Integer, BoxState> boxes;

    @Override
    public void writeToStream(ByteArrayOutputStreamWrapper stream) {
        stream.writeStringMapOfSerializable(vehicles);
        stream.writeIntegerMapOfSerializable(shells);
        stream.writeIntegerMapOfSerializable(missiles);
        stream.writeIntegerMapOfSerializable(drones);
        stream.writeIntegerMapOfSerializable(boxes);
    }
}
