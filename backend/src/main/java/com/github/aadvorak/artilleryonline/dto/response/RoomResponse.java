package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.Room;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class RoomResponse {

    private Set<RoomMemberResponse> members = new HashSet<>();

    private boolean deleted = false;

    private boolean opened = false;

    public static RoomResponse deletedOf(Room room) {
        return of(room).setDeleted(true);
    }

    public static RoomResponse of(Room room) {
        var response = new RoomResponse().setOpened(room.isOpened());
        response.members.add(RoomMemberResponse.of(room.getOwner(), true));
        room.getGuests().values().forEach(guest -> response.members.add(RoomMemberResponse.of(guest, false)));
        room.getBots().values().forEach(bot -> response.members.add(RoomMemberResponse.of(bot, false)));
        return response;
    }
}
