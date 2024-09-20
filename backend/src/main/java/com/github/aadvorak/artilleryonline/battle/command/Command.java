package com.github.aadvorak.artilleryonline.battle.command;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Command {
    START_MOVING,
    STOP_MOVING,
    PUSH_TRIGGER,
    RELEASE_TRIGGER,
    START_GUN_ROTATING,
    STOP_GUN_ROTATING,
    SELECT_SHELL
}
