package com.github.aadvorak.artilleryonline.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class Room {

    private BattleParticipant owner;

    private Map<Long, BattleParticipant> guests = new HashMap<>();

    private Map<String, BattleParticipant> bots = new HashMap<>();

    private Battle battle;

    public Set<BattleParticipant> getParticipants() {
        var participants = new HashSet<>(guests.values());
        participants.add(owner);
        participants.addAll(bots.values());
        return participants;
    }

    public int getParticipantsSize() {
        return guests.size() + bots.size() + 1;
    }
}
