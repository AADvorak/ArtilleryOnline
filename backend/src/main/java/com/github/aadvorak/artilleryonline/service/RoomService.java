package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.*;
import com.github.aadvorak.artilleryonline.collection.RoomInvitationMap;
import com.github.aadvorak.artilleryonline.collection.UserRoomMap;
import com.github.aadvorak.artilleryonline.dto.request.EnterRoomRequest;
import com.github.aadvorak.artilleryonline.dto.request.RoomInvitationRequest;
import com.github.aadvorak.artilleryonline.dto.response.RoomInvitationResponse;
import com.github.aadvorak.artilleryonline.dto.response.RoomResponse;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.error.exception.ConflictAppException;
import com.github.aadvorak.artilleryonline.error.exception.NotFoundAppException;
import com.github.aadvorak.artilleryonline.ws.RoomUpdatesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final UserService userService;

    private final UserRoomMap userRoomMap;

    private final RoomInvitationMap roomInvitationMap;

    private final OnlineUserService onlineUserService;

    private final BattleService battleService;

    private final RoomUpdatesSender roomUpdatesSender;

    public RoomResponse getRoom() {
        var user = userService.getUserFromContext();
        var room = userRoomMap.get(user.getId());
        if (room == null) {
            throw new NotFoundAppException();
        }
        log.info("getRoom: nickname {}, map size {}", user.getNickname(), userRoomMap.size());
        return RoomResponse.of(room);
    }

    public RoomResponse createRoom() {
        var user = userService.getUserFromContext();
        var existingRoom = userRoomMap.get(user.getId());
        if (existingRoom != null) {
            return RoomResponse.of(existingRoom);
        }
        var room = new Room();
        room.setOwner(BattleParticipant.of(user));
        userRoomMap.put(user.getId(), room);
        log.info("createRoom: nickname {}, map size {}", user.getNickname(), userRoomMap.size());
        return RoomResponse.of(room);
    }

    public RoomInvitationResponse inviteToRoom(RoomInvitationRequest request) {
        var user = userService.getUserFromContext();
        var existingRoom = userRoomMap.get(user.getId());
        if (existingRoom == null) {
            throw new NotFoundAppException();
        }
        var invitedUser = onlineUserService.findByNickname(request.getNickname())
                .orElseThrow(NotFoundAppException::new);
        var invitation = new RoomInvitation()
                .setRoom(existingRoom)
                .setUserId(invitedUser.getId());
        roomInvitationMap.put(invitation.getId(), invitation);
        var invitationResponse = RoomInvitationResponse.of(invitation);
        roomUpdatesSender.sendRoomInvitation(invitedUser, invitationResponse);
        log.info("inviteToRoom: from {}, to {}, invitation map size {}", user.getNickname(),
                invitedUser.getNickname(), roomInvitationMap.size());
        return invitationResponse;
    }

    public RoomResponse enterRoom(EnterRoomRequest request) {
        var invitation = roomInvitationMap.get(request.getInvitationId());
        if (invitation == null) {
            throw new NotFoundAppException();
        }
        var user = userService.getUserFromContext();
        if (invitation.getUserId() != user.getId()) {
            throw new NotFoundAppException();
        }
        roomInvitationMap.remove(request.getInvitationId());
        if (userRoomMap.get(invitation.getRoom().getOwner().getUser().getId()) == null) {
            throw new NotFoundAppException();
        }
        var existingRoom = userRoomMap.get(user.getId());
        if (existingRoom != null) {
            exitRoom(user, existingRoom);
        }
        var room = invitation.getRoom();
        room.getGuests().put(user.getId(), BattleParticipant.of(user));
        userRoomMap.put(user.getId(), room);
        log.info("enterRoom: nickname {}, map size {}, invitation map size {}", user.getNickname(),
                userRoomMap.size(), roomInvitationMap.size());
        roomUpdatesSender.sendRoomUpdate(room);
        return RoomResponse.of(room);
    }

    public void selectVehicle(BattleParticipantParams request) {
        var user = userService.getUserFromContext();
        var room = userRoomMap.get(user.getId());
        if (room == null) {
            throw new NotFoundAppException();
        }
        if (room.getOwner().getUser().getId() == user.getId()) {
            room.getOwner().setParams(request);
        } else {
            room.getGuests().get(user.getId()).setParams(request);
        }
        roomUpdatesSender.sendRoomUpdate(room);
    }

    public void startBattle() {
        var user = userService.getUserFromContext();
        var room = userRoomMap.get(user.getId());
        if (room == null) {
            throw new NotFoundAppException();
        }
        if (room.getOwner().getUser().getId() != user.getId()) {
            throw new NotFoundAppException();
        }
        if (room.getGuests().isEmpty()) {
            throw new ConflictAppException("Not enough players to start battle");
        }
        battleService.createRoomBattle(room);
        roomUpdatesSender.sendRoomUpdate(room);
    }

    public void exitRoom() {
        var user = userService.getUserFromContext();
        var room = userRoomMap.get(user.getId());
        if (room != null) {
            exitRoom(user, room);
        }
    }

    private void exitRoom(User user, Room room) {
        if (room.getOwner().getUser().getId() != user.getId()) {
            userRoomMap.remove(user.getId());
            room.getGuests().remove(user.getId());
            roomUpdatesSender.sendRoomUpdate(room);
            log.info("exitRoom: nickname {}, map size {}", user.getNickname(), userRoomMap.size());
        } else {
            var userIds = new HashSet<Long>();
            userIds.add(user.getId());
            room.getGuests().values().forEach(guest -> userIds.add(guest.getUser().getId()));
            userIds.forEach(userRoomMap::remove);
            roomUpdatesSender.sendRoomUpdate(room, true);
            log.info("exitRoom: (room deleted) nickname {}, map size {}", user.getNickname(), userRoomMap.size());
        }
    }
}
