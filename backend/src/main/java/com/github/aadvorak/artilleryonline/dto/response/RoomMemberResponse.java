package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.BattleParticipant;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RoomMemberResponse {

    private String nickname;

    private String selectedVehicle;

    private boolean owner;

    public static RoomMemberResponse of(BattleParticipant participant, boolean owner) {
        return new RoomMemberResponse()
                .setNickname(participant.getNickname())
                .setSelectedVehicle(participant.getParams() != null ? participant.getParams().getSelectedVehicle() : null)
                .setOwner(owner);
    }
}
