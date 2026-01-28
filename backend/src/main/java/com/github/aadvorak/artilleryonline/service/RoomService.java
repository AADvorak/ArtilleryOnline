package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.*;
import com.github.aadvorak.artilleryonline.collection.UserRoomMap;
import com.github.aadvorak.artilleryonline.dto.response.RoomResponse;
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

import java.util.HashSet;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final UserService userService;

    private final UserRoomMap userRoomMap;

    private final BattleService battleService;

    private final RoomUpdatesSender roomUpdatesSender;

    private final MessageService messageService;

    private final ApplicationLimits applicationLimits;

    private final BotsService botsService = new BotsService();

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
        var room = requireOwnRoom(user);
        if (room.getParticipantsSize() < 2) {
            throw new ConflictAppException("Not enough players to start battle",
                    new Locale().setCode(LocaleCode.NOT_ENOUGH_PLAYERS));
        }
        battleService.createRoomBattle(room);
        removeSelectedVehicles(room);
    }

    public void exitRoom() {
        var user = userService.getUserFromContext();
        var room = userRoomMap.get(user.getId());
        if (room != null) {
            exitRoom(user, room);
        }
    }

    public void removeParticipant(String nickname) {
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
            log.info("removeUserFromRoom: nickname {}, removed by {}, map size {}", nickname,
                    user.getNickname(), userRoomMap.size());
        });
        var botToRemove = room.getBots().values().stream()
                .filter(bot -> bot.getNickname().equals(nickname))
                .findAny();
        botToRemove.ifPresent(bot -> {
            room.getBots().remove(bot.getNickname());
            roomUpdatesSender.sendRoomUpdate(room);
            log.info("removeBotFromRoom: nickname {}, removed by {}", nickname, user.getNickname());
        });
    }

    public Room requireOwnRoom(User user) {
        var room = userRoomMap.get(user.getId());
        if (room == null) {
            throw new NotFoundAppException();
        }
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
            log.info("exitRoom: nickname {}, map size {}", user.getNickname(), userRoomMap.size());
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
            roomUpdatesSender.sendRoomUpdate(room, true);
            log.info("exitRoom: (room deleted) nickname {}, map size {}", user.getNickname(), userRoomMap.size());
        }
    }

    public void addBot() {
        var user = userService.getUserFromContext();
        var room = requireOwnRoom(user);
        if (room.getParticipantsSize() >= applicationLimits.getMaxRoomMembers()) {
            throw new ConflictAppException("Room is already full",
                    new Locale().setCode(LocaleCode.ROOM_IS_FULL));
        }
        var bot = botsService.generateBot(room.getParticipants());
        room.getBots().put(bot.getNickname(), bot);
        roomUpdatesSender.sendRoomUpdate(room);
    }

    private void removeSelectedVehicles(Room room) {
        room.getOwner().getParams().setSelectedVehicle(null);
        room.getGuests().values().forEach(guest -> guest.getParams().setSelectedVehicle(null));
    }
}
