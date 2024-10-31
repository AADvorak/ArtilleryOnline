package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.command.DebugCommand;
import com.github.aadvorak.artilleryonline.battle.command.UserCommand;
import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueue;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
@Setter
public class BattleQueues {

    private final Queue<DebugCommand> debugCommands = new ConcurrentLinkedQueue<>();

    private final BattleUpdatesQueue battleUpdatesQueue = new BattleUpdatesQueue();

    private Map<Long, Queue<UserCommand>> userCommandQueues;
}
