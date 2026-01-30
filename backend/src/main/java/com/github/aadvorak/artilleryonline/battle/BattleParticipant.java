package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.collection.UserBattleQueueElement;
import com.github.aadvorak.artilleryonline.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleParticipant {

    private String nickname;

    private User user;

    private BattleParticipantParams params;

    private int teamId;

    public static BattleParticipant of(UserBattleQueueElement queueElement) {
       return new BattleParticipant()
               .setUser(queueElement.getUser())
               .setNickname(queueElement.getUser().getNickname())
               .setParams(new BattleParticipantParams()
                       .setSelectedVehicle(queueElement.getParams().getSelectedVehicle()));
    }

    public static BattleParticipant of(User user) {
        return new BattleParticipant()
                .setUser(user)
                .setNickname(user.getNickname());
    }
}
