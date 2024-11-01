package com.github.aadvorak.artilleryonline.collection;

import com.github.aadvorak.artilleryonline.model.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMessageMap {

    private final HashMap<Long, Map<String, Message>> map = new HashMap<>();

    public void add(Long userId, Message message) {
        computeIfAbsent(userId).put(message.getId(), message);
    }

    public List<Message> get(Long userId) {
        return computeIfAbsent(userId).values().stream().toList();
    }

    public void remove(Long userId, String messageId) {
        computeIfAbsent(userId).remove(messageId);
    }

    private Map<String, Message> computeIfAbsent(Long userId) {
        return map.computeIfAbsent(userId, k -> new HashMap<>());
    }
}
