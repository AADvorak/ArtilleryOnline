package com.github.aadvorak.artilleryonline.battle.specs;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class VehicleSpecs implements Specs {

    private double hitPoints;

    private int ammo;

    private double minAngle;

    private double maxAngle;

    /**
     * todo remove, one friction for all vehicles
     */
    private double movingVelocity;

    private double acceleration;

    private double radius;

    private double wheelRadius;

    private double hullRadius;

    private double trackRepairTime;

    private Map<String, GunSpecs> availableGuns;
}
