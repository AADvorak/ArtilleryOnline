package com.github.aadvorak.artilleryonline.collection;

import com.github.aadvorak.artilleryonline.dto.response.BattleStateResponse;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BattleStateUpdatesQueue extends ConcurrentLinkedQueue<BattleStateResponse> {
}
