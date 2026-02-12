package com.github.aadvorak.artilleryonline.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Stream;

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

    private BattleType battleType = BattleType.DEATHMATCH;

    public Set<BattleParticipant> getMembers() {
        var members = new HashSet<>(guests.values());
        members.add(owner);
        members.addAll(bots.values());
        return members;
    }

    public int getMembersCount() {
        return guests.size() + bots.size() + 1;
    }

    public int getTeamMembersCount(int teamId) {
        return (int) getMembers().stream()
                .filter(member -> teamId == member.getTeamId())
                .count();
    }

    public Integer getSmallestTeamId() {
        var counts = Stream.of(0, 1)
                .map(this::getTeamMembersCount)
                .toList();
        return counts.get(0) <= counts.get(1) ? 0 : 1;
    }
}
