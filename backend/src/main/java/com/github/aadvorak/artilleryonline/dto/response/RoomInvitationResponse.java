package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.RoomInvitation;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RoomInvitationResponse {

    private String id;

    private String inviterNickname;

    public static RoomInvitationResponse of(RoomInvitation roomInvitation) {
        return new RoomInvitationResponse()
                .setId(roomInvitation.getId())
                .setInviterNickname(roomInvitation.getRoom().getOwner().getNickname());
    }
}
