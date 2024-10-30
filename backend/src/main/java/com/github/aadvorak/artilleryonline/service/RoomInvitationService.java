package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.BattleParticipant;
import com.github.aadvorak.artilleryonline.battle.RoomInvitation;
import com.github.aadvorak.artilleryonline.collection.RoomInvitationMap;
import com.github.aadvorak.artilleryonline.collection.UserRoomMap;
import com.github.aadvorak.artilleryonline.dto.request.RoomInvitationRequest;
import com.github.aadvorak.artilleryonline.dto.response.RoomInvitationResponse;
import com.github.aadvorak.artilleryonline.dto.response.RoomResponse;
import com.github.aadvorak.artilleryonline.error.exception.NotFoundAppException;
import com.github.aadvorak.artilleryonline.ws.RoomUpdatesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomInvitationService {

    private final UserService userService;

    private final UserRoomMap userRoomMap;

    private final RoomInvitationMap roomInvitationMap;

    private final OnlineUserService onlineUserService;

    private final RoomService roomService;

    private final RoomUpdatesSender roomUpdatesSender;

    private final UserAvailabilityService userAvailabilityService;

    public List<RoomInvitationResponse> getUserInvitations() {
        var user = userService.getUserFromContext();
        // todo possible slow query
        return roomInvitationMap.values().stream()
                .filter(invitation -> invitation.getUserId() == user.getId())
                .map(RoomInvitationResponse::of)
                .toList();
    }

    public RoomInvitationResponse inviteToRoom(RoomInvitationRequest request) {
        var user = userService.getUserFromContext();
        var room = roomService.requireOwnRoom(user);
        var invitedUser = onlineUserService.findByNickname(request.getNickname())
                .orElseThrow(NotFoundAppException::new);
        var invitation = new RoomInvitation()
                .setRoom(room)
                .setUserId(invitedUser.getId());
        roomInvitationMap.put(invitation.getId(), invitation);
        var invitationResponse = RoomInvitationResponse.of(invitation);
        roomUpdatesSender.sendRoomInvitation(invitedUser, invitationResponse);
        log.info("inviteToRoom: from {}, to {}, invitation map size {}", user.getNickname(),
                invitedUser.getNickname(), roomInvitationMap.size());
        return invitationResponse;
    }

    public RoomResponse acceptInvitation(String invitationId) {
        var invitation = roomInvitationMap.get(invitationId);
        if (invitation == null) {
            throw new NotFoundAppException();
        }
        var user = userService.getUserFromContext();
        if (invitation.getUserId() != user.getId()) {
            throw new NotFoundAppException();
        }
        roomInvitationMap.remove(invitationId);
        if (userRoomMap.get(invitation.getRoom().getOwner().getUser().getId()) == null) {
            throw new NotFoundAppException();
        }
        userAvailabilityService.checkRoomAvailability(user);
        var existingRoom = userRoomMap.get(user.getId());
        if (existingRoom != null) {
            if (existingRoom.getGuests().containsKey(user.getId())) {
                return RoomResponse.of(existingRoom);
            }
            roomService.exitRoom(user, existingRoom);
        }
        var room = invitation.getRoom();
        room.getGuests().put(user.getId(), BattleParticipant.of(user));
        userRoomMap.put(user.getId(), room);
        log.info("acceptInvitation: nickname {}, map size {}, invitation map size {}", user.getNickname(),
                userRoomMap.size(), roomInvitationMap.size());
        roomUpdatesSender.sendRoomUpdate(room);
        return RoomResponse.of(room);
    }

    public void deleteInvitation(String invitationId) {
        var invitation = roomInvitationMap.get(invitationId);
        if (invitation == null) {
            throw new NotFoundAppException();
        }
        var user = userService.getUserFromContext();
        if (invitation.getUserId() != user.getId()) {
            throw new NotFoundAppException();
        }
        roomInvitationMap.remove(invitationId);
        log.info("deleteInvitation: nickname {}, invitation map size {}", user.getNickname(),
                roomInvitationMap.size());
    }
}
