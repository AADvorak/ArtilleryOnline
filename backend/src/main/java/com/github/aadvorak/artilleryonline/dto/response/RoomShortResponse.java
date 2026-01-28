package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.Room;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RoomShortResponse {

    private String id;

    private String ownerNickname;

    private int membersCount;

    private boolean inBattle;

    public static RoomShortResponse of(Room room) {
        return new RoomShortResponse()
                .setId(room.getId())
                .setOwnerNickname(room.getOwner().getNickname())
                .setMembersCount(room.getParticipantsSize())
                .setInBattle(room.getBattle() != null);
    }
}
