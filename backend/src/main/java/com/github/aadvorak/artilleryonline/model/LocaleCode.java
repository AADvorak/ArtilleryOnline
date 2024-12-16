package com.github.aadvorak.artilleryonline.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LocaleCode {

    USER_REMOVED_FROM_ROOM("userRemovedFromRoom"),
    USER_LEFT_ROOM("userLeftRoom"),
    USER_ENTERED_ROOM("userEnteredRoom"),
    USER_LEFT_AND_DELETED_ROOM("userLeftAndDeletedRoom"),
    USER_LEFT_BATTLE("userLeftBattle"),
    BATTLE_FINISHED("battleFinished"),

    NO_SELECTED_VEHICLES("noSelectedVehicles"),
    NOT_ENOUGH_PLAYERS("notEnoughPlayers"),
    USER_ALREADY_IN_ROOM("userAlreadyInRoom"),
    USER_ALREADY_IN_BATTLE_QUEUE("userAlreadyInBattleQueue"),
    USER_ALREADY_IN_BATTLE("userAlreadyInBattle"),;

    private final String value;
}
