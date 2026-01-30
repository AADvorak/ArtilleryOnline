package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.Room;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class RoomResponse {

    private List<Set<RoomMemberResponse>> members = new ArrayList<>();

    private boolean deleted = false;

    private boolean opened = false;

    private boolean teamMode = false;

    public static RoomResponse deletedOf(Room room) {
        return of(room).setDeleted(true);
    }

    public static RoomResponse of(Room room) {
        var response = new RoomResponse()
                .setOpened(room.isOpened())
                .setTeamMode(room.isTeamMode());
        response.members.add(new HashSet<>());
        if (response.isTeamMode()) {
            response.members.add(new HashSet<>());
        }
        response.members.get(room.getOwner().getTeamId()).add(RoomMemberResponse.of(room.getOwner(), true));
        room.getGuests().values().forEach(guest ->
                response.members.get(guest.getTeamId()).add(RoomMemberResponse.of(guest, false)));
        room.getBots().values().forEach(bot ->
                response.members.get(bot.getTeamId()).add(RoomMemberResponse.of(bot, false)));
        return response;
    }
}
