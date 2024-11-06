package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.battle.Room;
import com.github.aadvorak.artilleryonline.dto.response.RoomInvitationResponse;
import com.github.aadvorak.artilleryonline.dto.response.RoomResponse;
import com.github.aadvorak.artilleryonline.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class RoomUpdatesSender {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void sendRoomInvitation(User invitedUser, RoomInvitationResponse invitationResponse) {
        simpMessagingTemplate.convertAndSendToUser(invitedUser.getEmail(),
                "/topic/room/invitations", invitationResponse);
    }

    public void sendRoomUpdate(Room room) {
        sendRoomUpdate(room, false, false);
    }

    public void sendRoomUpdate(Room room, boolean deleted, boolean inBattle) {
        var emails = new ArrayList<String>();
        emails.add(room.getOwner().getUser().getEmail());
        room.getGuests().values().forEach(guest -> emails.add(guest.getUser().getEmail()));
        var roomResponse = deleted ? RoomResponse.deletedOf(room) :
                (inBattle ? RoomResponse.inBattleOf(room) : RoomResponse.of(room));
        emails.forEach(email -> simpMessagingTemplate.convertAndSendToUser(email,
                "/topic/room/updates", roomResponse));
    }

    public void sendRoomDelete(Room room, User user) {
        simpMessagingTemplate.convertAndSendToUser(user.getEmail(),
                "/topic/room/updates", RoomResponse.deletedOf(room));
    }
}
