package com.github.aadvorak.artilleryonline.collection;

import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BattleUpdatesQueue extends ConcurrentLinkedQueue<BattleResponse> {
}
