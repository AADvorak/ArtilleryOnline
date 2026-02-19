package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.battle.ChatMessage;
import com.github.aadvorak.artilleryonline.battle.Room;
import com.github.aadvorak.artilleryonline.dto.response.ChatMessageResponse;
import com.github.aadvorak.artilleryonline.dto.response.RoomInvitationResponse;
import com.github.aadvorak.artilleryonline.dto.response.RoomResponse;
import com.github.aadvorak.artilleryonline.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomUpdatesSender {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void sendRoomInvitation(User invitedUser, RoomInvitationResponse invitationResponse) {
        simpMessagingTemplate.convertAndSendToUser(invitedUser.getEmail(),
                "/topic/room/invitations", invitationResponse);
    }

    public void sendRoomUpdate(Room room) {
        sendRoomUpdate(room, false);
    }

    public void sendRoomUpdate(Room room, boolean deleted) {
        var emails = getEmails(room);
        var roomResponse = deleted ? RoomResponse.deletedOf(room) : RoomResponse.of(room);
        emails.forEach(email -> simpMessagingTemplate.convertAndSendToUser(email,
                "/topic/room/updates", roomResponse));
    }

    public void sendRoomDelete(Room room, User user) {
        simpMessagingTemplate.convertAndSendToUser(user.getEmail(),
                "/topic/room/updates", RoomResponse.deletedOf(room));
    }

    public void sendRoomMessage(Room room, ChatMessage chatMessage) {
        var emails = getEmails(room);
        var messageResponse = ChatMessageResponse.of(chatMessage);
        emails.forEach(email -> simpMessagingTemplate.convertAndSendToUser(email,
                "/topic/room/messages", messageResponse));
    }

    private List<String> getEmails(Room room) {
        var emails = new ArrayList<String>();
        emails.add(room.getOwner().getUser().getEmail());
        room.getGuests().values().forEach(guest -> emails.add(guest.getUser().getEmail()));
        return emails;
    }
}
