package com.github.aadvorak.artilleryonline.battle.updates;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleModelRemoved {

    private List<Integer> shellIds;

    private List<Integer> explosionIds;

    private List<String> vehicleKeys;

    public void addShellId(Integer shellId) {
        if (shellIds == null) {
            shellIds = new ArrayList<>();
        }
        shellIds.add(shellId);
    }

    public void addExplosionId(Integer explosionId) {
        if (explosionIds == null) {
            explosionIds = new ArrayList<>();
        }
        explosionIds.add(explosionId);
    }

    public void addVehicleKey(String vehicleKey) {
        if (vehicleKeys == null) {
            vehicleKeys = new ArrayList<>();
        }
        vehicleKeys.add(vehicleKey);
    }
}
