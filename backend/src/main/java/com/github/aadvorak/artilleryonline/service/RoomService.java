package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.BattleParticipant;
import com.github.aadvorak.artilleryonline.battle.BattleParticipantParams;
import com.github.aadvorak.artilleryonline.battle.Room;
import com.github.aadvorak.artilleryonline.collection.RoomMap;
import com.github.aadvorak.artilleryonline.collection.UserRoomMap;
import com.github.aadvorak.artilleryonline.dto.response.RoomResponse;
import com.github.aadvorak.artilleryonline.dto.response.RoomShortResponse;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.error.exception.ConflictAppException;
import com.github.aadvorak.artilleryonline.error.exception.NotFoundAppException;
import com.github.aadvorak.artilleryonline.model.Locale;
import com.github.aadvorak.artilleryonline.model.LocaleCode;
import com.github.aadvorak.artilleryonline.properties.ApplicationLimits;
import com.github.aadvorak.artilleryonline.ws.RoomUpdatesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final UserService userService;

    private final RoomMap roomMap;

    private final UserRoomMap userRoomMap;

    private final BattleService battleService;

    private final RoomUpdatesSender roomUpdatesSender;

    private final MessageService messageService;

    private final ApplicationLimits applicationLimits;

    private final UserAvailabilityService userAvailabilityService;

    private final BotsService botsService = new BotsService();

    public List<RoomShortResponse> getOpenRooms() {
        return roomMap.values().stream()
                .filter(Room::isOpened)
                .map(RoomShortResponse::of)
                .sorted(Comparator.comparingInt(RoomShortResponse::getMembersCount))
                .toList();
    }

    public RoomResponse enterRoom(String id) {
        var room = roomMap.get(id);
        if (room == null) {
            throw new NotFoundAppException();
        }
        var user = userService.getUserFromContext();
        return enterRoom(user, room);
    }

    public RoomResponse enterRoom(User user, Room room) {
        if (room.getMembersCount() >= applicationLimits.getMaxRoomMembers()) {
            throw new ConflictAppException("Room is already full",
                    new Locale().setCode(LocaleCode.ROOM_IS_FULL));
        }
        userAvailabilityService.checkRoomAvailability(user);
        var existingRoom = getUserRoomOrNull(user.getId());
        if (existingRoom != null) {
            if (existingRoom.getGuests().containsKey(user.getId())
                    || room.getOwner().getUser().getId() == user.getId()) {
                return RoomResponse.of(existingRoom);
            }
            exitRoom(user, existingRoom);
        }
        var member = BattleParticipant.of(user);
        if (room.isTeamMode()) {
            member.setTeamId(room.getSmallestTeamId());
        }
        room.getGuests().put(user.getId(), member);
        userRoomMap.put(user.getId(), room.getId());
        log.info("enterRoom: nickname {}, users in rooms size {}, rooms size {}",
                user.getNickname(), userRoomMap.size(), roomMap.size());
        roomUpdatesSender.sendRoomUpdate(room);
        messageService.createMessage(room.getOwner().getUser(),
                "User " + user.getNickname() + " entered room",
                new Locale()
                        .setCode(LocaleCode.USER_ENTERED_ROOM)
                        .setParams(Map.of("nickname", user.getNickname())));
        return RoomResponse.of(room);
    }

    public RoomResponse getRoom() {
        var user = userService.getUserFromContext();
        var room = requireUserRoom(user.getId());
        return RoomResponse.of(room);
    }

    public RoomResponse createRoom() {
        var user = userService.getUserFromContext();
        var existingRoom = getUserRoomOrNull(user.getId());
        if (existingRoom != null) {
            return RoomResponse.of(existingRoom);
        }
        var room = new Room();
        room.setOwner(BattleParticipant.of(user));
        roomMap.put(room.getId(), room);
        userRoomMap.put(user.getId(), room.getId());
        log.info("createRoom: nickname {}, users in rooms size {}, rooms size {}",
                user.getNickname(), userRoomMap.size(), roomMap.size());
        return RoomResponse.of(room);
    }

    public void selectVehicle(BattleParticipantParams request) {
        var user = userService.getUserFromContext();
        var room = requireUserRoom(user.getId());
        if (room.getOwner().getUser().getId() == user.getId()) {
            room.getOwner().setParams(request);
        } else {
            room.getGuests().get(user.getId()).setParams(request);
        }
        roomUpdatesSender.sendRoomUpdate(room);
    }

    public void startBattle() {
        var user = userService.getUserFromContext();
        var room = requireOwnRoom(user);
        if (room.getMembersCount() < 2) {
            throw new ConflictAppException("Not enough players to start battle",
                    new Locale().setCode(LocaleCode.NOT_ENOUGH_PLAYERS));
        }
        battleService.createRoomBattle(room);
        removeSelectedVehicles(room);
    }

    public void exitRoom() {
        var user = userService.getUserFromContext();
        getUserRoom(user.getId()).ifPresent(room -> exitRoom(user, room));
    }

    public void removeMember(String nickname) {
        var user = userService.getUserFromContext();
        var room = requireOwnRoom(user);
        var guestToRemove = room.getGuests().values().stream()
                .filter(guest -> guest.getNickname().equals(nickname))
                .findAny();
        guestToRemove.ifPresent(guest -> {
            room.getGuests().remove(guest.getUser().getId());
            userRoomMap.remove(guest.getUser().getId());
            messageService.createMessage(guest.getUser(),
                    "User " + user.getNickname() + " removed you from the room",
                    new Locale()
                            .setCode(LocaleCode.USER_REMOVED_FROM_ROOM)
                            .setParams(Map.of("nickname", user.getNickname())));
            roomUpdatesSender.sendRoomUpdate(room);
            roomUpdatesSender.sendRoomDelete(room, guest.getUser());
            log.info("removeUserFromRoom: nickname {}, removed by {}, users in rooms size {}, rooms size {}",
                    nickname, user.getNickname(), userRoomMap.size(), roomMap.size());
        });
        var botToRemove = room.getBots().values().stream()
                .filter(bot -> bot.getNickname().equals(nickname))
                .findAny();
        botToRemove.ifPresent(bot -> {
            room.getBots().remove(bot.getNickname());
            roomUpdatesSender.sendRoomUpdate(room);
        });
    }

    public void changeMembersTeam(String nickname, int teamId) {
        var user = userService.getUserFromContext();
        var room = requireUserRoom(user.getId());
        if (!user.getNickname().equals(nickname) && room.getOwner().getUser().getId() != user.getId()) {
            throw new ConflictAppException("Unable to change members team",
                    new Locale().setCode(LocaleCode.UNABLE_TO_CHANGE_MEMBERS_TEAM));
        }
        var member = room.getMembers().stream()
                .filter(participant -> participant.getNickname().equals(nickname))
                .findAny()
                .orElseThrow(NotFoundAppException::new);
        member.setTeamId(teamId);
        roomUpdatesSender.sendRoomUpdate(room);
    }

    public Room requireOwnRoom(User user) {
        var room = requireUserRoom(user.getId());
        if (room.getOwner().getUser().getId() != user.getId()) {
            throw new NotFoundAppException();
        }
        return room;
    }

    public void exitRoom(User user, Room room) {
        if (room.getOwner().getUser().getId() != user.getId()) {
            userRoomMap.remove(user.getId());
            room.getGuests().remove(user.getId());
            roomUpdatesSender.sendRoomUpdate(room);
            messageService.createMessage(room.getOwner().getUser(),
                    "User " + user.getNickname() + " left the room",
                    new Locale()
                            .setCode(LocaleCode.USER_LEFT_ROOM)
                            .setParams(Map.of("nickname", user.getNickname())));
            log.info("exitRoom: nickname {}, users in rooms size {}, rooms size {}",
                    user.getNickname(), userRoomMap.size(), roomMap.size());
        } else {
            var userIds = new HashSet<Long>();
            userIds.add(user.getId());
            room.getGuests().values().forEach(guest -> {
                userIds.add(guest.getUser().getId());
                messageService.createMessage(guest.getUser(),
                        "User " + user.getNickname() + " left and deleted the room",
                        new Locale()
                                .setCode(LocaleCode.USER_LEFT_AND_DELETED_ROOM)
                                .setParams(Map.of("nickname", user.getNickname())));
            });
            userIds.forEach(userRoomMap::remove);
            roomMap.remove(room.getId());
            roomUpdatesSender.sendRoomUpdate(room, true);
            log.info("exitRoom: (room deleted) nickname {}, users in rooms size {}, rooms size {}",
                    user.getNickname(), userRoomMap.size(), roomMap.size());
        }
    }

    public void addBot() {
        var user = userService.getUserFromContext();
        var room = requireOwnRoom(user);
        if (room.getMembersCount() >= applicationLimits.getMaxRoomMembers()) {
            throw new ConflictAppException("Room is already full",
                    new Locale().setCode(LocaleCode.ROOM_IS_FULL));
        }
        var bot = botsService.generateBot(room.getMembers());
        if (room.isTeamMode()) {
            bot.setTeamId(room.getSmallestTeamId());
        }
        room.getBots().put(bot.getNickname(), bot);
        roomUpdatesSender.sendRoomUpdate(room);
    }

    public void changeOpened(boolean opened) {
        var user = userService.getUserFromContext();
        var room = requireOwnRoom(user);
        room.setOpened(opened);
        roomUpdatesSender.sendRoomUpdate(room);
    }

    public void changeTeamMode(boolean teamMode) {
        var user = userService.getUserFromContext();
        var room = requireOwnRoom(user);
        room.setTeamMode(teamMode);
        if (teamMode) {
            var index = 0;
            for (var member : room.getMembers()) {
                member.setTeamId(index % 2 == 0 ? 0 : 1);
                index++;
            }
        } else {
            for (var member : room.getMembers()) {
                member.setTeamId(0);
            }
        }
        roomUpdatesSender.sendRoomUpdate(room);
    }

    private Room getUserRoomOrNull(long userId) {
        return getUserRoom(userId).orElse(null);
    }

    private Room requireUserRoom(long userId) {
        return getUserRoom(userId).orElseThrow(NotFoundAppException::new);
    }

    private Optional<Room> getUserRoom(long userId) {
        return Optional.ofNullable(userRoomMap.get(userId)).map(roomMap::get);
    }

    private void removeSelectedVehicles(Room room) {
        room.getOwner().getParams().setSelectedVehicle(null);
        room.getGuests().values().forEach(guest -> guest.getParams().setSelectedVehicle(null));
    }
}
