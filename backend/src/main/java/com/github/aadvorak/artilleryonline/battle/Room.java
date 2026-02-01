package com.github.aadvorak.artilleryonline.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;

@Getter
@Setter
@Accessors(chain = true)
public class Room {

    private final String id = UUID.randomUUID().toString();

    private BattleParticipant owner;

    private Map<Long, BattleParticipant> guests = new HashMap<>();

    private Map<String, BattleParticipant> bots = new HashMap<>();

    private Battle battle;

    private boolean opened = false;

    private boolean teamMode = false;

    public Set<BattleParticipant> getMembers() {
        var participants = new HashSet<>(guests.values());
        participants.add(owner);
        participants.addAll(bots.values());
        return participants;
    }

    public int getMembersCount() {
        return guests.size() + bots.size() + 1;
    }
}
