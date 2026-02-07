package com.github.aadvorak.artilleryonline.battle;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class BattleBotsData {

    private Set<Integer> vehicleIds = new HashSet<>();

    private long lastTimeProcessed = 0;
}
