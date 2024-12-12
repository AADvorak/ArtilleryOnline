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
    USER_LEFT_BATTLE("userLeftBattle"),;

    private final String value;
}
