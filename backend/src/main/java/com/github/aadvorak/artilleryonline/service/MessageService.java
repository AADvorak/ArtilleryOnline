package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.collection.UserMessageMap;
import com.github.aadvorak.artilleryonline.dto.response.MessageResponse;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.model.Locale;
import com.github.aadvorak.artilleryonline.model.Message;
import com.github.aadvorak.artilleryonline.model.MessageSpecial;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import com.github.aadvorak.artilleryonline.ws.MessagesSender;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserMessageMap userMessageMap;

    private final MessagesSender messagesSender;

    private final UserService userService;

    private final ApplicationSettings applicationSettings;

    private final TaskScheduler taskScheduler =
            new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());

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

    public void createMessage(User user, String text, Locale locale) {
        createMessage(user, text, locale, null);
    }

    public void createMessage(User user, String text, Locale locale, MessageSpecial special) {
        var message = new Message()
                .setUserId(user.getId())
                .setText(text)
                .setLocale(locale)
                .setSpecial(special);
        userMessageMap.add(user.getId(), message);
        messagesSender.sendMessage(user, MessageResponse.of(message));
        scheduleDeleteMessage(message);
    }

    private void scheduleDeleteMessage(Message message) {
        taskScheduler.schedule(() -> userMessageMap.remove(message.getUserId(), message.getId()),
                Instant.ofEpochMilli(message.getCreateTime() + applicationSettings.getMessageTimeout()));
    }
}
