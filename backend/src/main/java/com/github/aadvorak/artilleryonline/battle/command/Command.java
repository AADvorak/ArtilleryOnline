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
    SELECT_SHELL,
    JET_ON,
    JET_OFF,
    LAUNCH_MISSILE,
    LAUNCH_DRONE,
    SWITCH_GUN_MODE,

    PAUSE,
    RESUME,
    STEP,
    START_TRACKING,
    STOP_TRACKING,

    START_PUSHING,
    STOP_PUSHING,
    START_ROTATING,
    STOP_ROTATING,
    STOP_ALL,
    SWITCH_BODY,
}
