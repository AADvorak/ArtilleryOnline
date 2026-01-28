package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.BattleParticipant;
import com.github.aadvorak.artilleryonline.battle.RoomInvitation;
import com.github.aadvorak.artilleryonline.collection.RoomInvitationMap;
import com.github.aadvorak.artilleryonline.collection.UserRoomMap;
import com.github.aadvorak.artilleryonline.dto.request.RoomInvitationRequest;
import com.github.aadvorak.artilleryonline.dto.response.RoomInvitationResponse;
import com.github.aadvorak.artilleryonline.dto.response.RoomResponse;
import com.github.aadvorak.artilleryonline.error.exception.ConflictAppException;
import com.github.aadvorak.artilleryonline.error.exception.NotFoundAppException;
import com.github.aadvorak.artilleryonline.model.Locale;
import com.github.aadvorak.artilleryonline.model.LocaleCode;
import com.github.aadvorak.artilleryonline.properties.ApplicationLimits;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import com.github.aadvorak.artilleryonline.ws.RoomUpdatesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

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

    private final MessageService messageService;

    private final ApplicationSettings applicationSettings;

    private final ApplicationLimits applicationLimits;

    private final TaskScheduler taskScheduler =
            new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());

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
        scheduleDeleteInvitation(invitation);
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
        var room = invitation.getRoom();
        if (room.getParticipantsSize() >= applicationLimits.getMaxRoomMembers()) {
            throw new ConflictAppException("Room is already full",
                    new Locale().setCode(LocaleCode.ROOM_IS_FULL));
        }
        var existingRoom = userRoomMap.get(user.getId());
        if (existingRoom != null) {
            if (existingRoom.getGuests().containsKey(user.getId())) {
                return RoomResponse.of(existingRoom);
            }
            roomService.exitRoom(user, existingRoom);
        }
        room.getGuests().put(user.getId(), BattleParticipant.of(user));
        userRoomMap.put(user.getId(), room);
        log.info("acceptInvitation: nickname {}, map size {}, invitation map size {}", user.getNickname(),
                userRoomMap.size(), roomInvitationMap.size());
        roomUpdatesSender.sendRoomUpdate(room);
        messageService.createMessage(invitation.getRoom().getOwner().getUser(),
                "User " + user.getNickname() + " entered room",
                new Locale()
                        .setCode(LocaleCode.USER_ENTERED_ROOM)
                        .setParams(Map.of("nickname", user.getNickname())));
        return RoomResponse.of(room);
    }

    public void deleteInvitation(String invitationId) {
        var invitation = roomInvitationMap.get(invitationId);
        if (invitation == null) {
            return;
        }
        var user = userService.getUserFromContext();
        if (invitation.getUserId() != user.getId()) {
            return;
        }
        roomInvitationMap.remove(invitationId);
        log.info("deleteInvitation: nickname {}, invitation map size {}", user.getNickname(),
                roomInvitationMap.size());
    }

    private void scheduleDeleteInvitation(RoomInvitation invitation) {
        taskScheduler.schedule(() -> {
            roomInvitationMap.remove(invitation.getId());
            log.info("scheduleDeleteInvitation: id {}, invitation map size {}", invitation.getId(),
                    roomInvitationMap.size());
        }, Instant.ofEpochMilli(invitation.getCreateTime() + applicationSettings.getRoomInvitationTimeout()));
    }
}
