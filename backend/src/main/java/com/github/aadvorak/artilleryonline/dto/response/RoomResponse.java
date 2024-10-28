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

    private boolean inBattle;

    private boolean deleted = false;

    public static RoomResponse deletedOf(Room room) {
        return of(room).setDeleted(true);
    }

    public static RoomResponse of(Room room) {
        var response = new RoomResponse();
        response.setInBattle(room.getBattle() != null);
        response.members.add(RoomMemberResponse.of(room.getOwner(), true));
        room.getGuests().values().forEach(guest -> response.members.add(RoomMemberResponse.of(guest, false)));
        return response;
    }
}
