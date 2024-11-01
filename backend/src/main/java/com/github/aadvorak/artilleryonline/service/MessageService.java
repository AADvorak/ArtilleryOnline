package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.collection.UserMessageMap;
import com.github.aadvorak.artilleryonline.dto.response.MessageResponse;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.model.Message;
import com.github.aadvorak.artilleryonline.ws.MessagesSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserMessageMap userMessageMap;

    private final MessagesSender messagesSender;

    private final UserService userService;

    public List<MessageResponse> getMessages() {
        var user = userService.getUserFromContext();
        return userMessageMap.get(user.getId()).stream()
                .sorted(Comparator.comparing(Message::getCreateTime).reversed())
                .map(MessageResponse::of)
                .toList();
    }

    public void deleteMessage(String messageId) {
        var user = userService.getUserFromContext();
        userMessageMap.remove(user.getId(), messageId);
    }

    public void createMessage(User user, String text) {
        var message = new Message()
                .setUserId(user.getId())
                .setText(text);
        userMessageMap.add(user.getId(), message);
        messagesSender.sendMessage(user, MessageResponse.of(message));
    }
}
