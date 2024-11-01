package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.dto.response.MessageResponse;
import com.github.aadvorak.artilleryonline.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageEndpoint {

    private final MessageService messageService;

    @GetMapping
    public List<MessageResponse> getMessages() {
        return messageService.getMessages();
    }

    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable String messageId) {
        messageService.deleteMessage(messageId);
    }
}
