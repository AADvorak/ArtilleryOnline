package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.battle.BattleParticipantParams;
import com.github.aadvorak.artilleryonline.dto.response.RoomResponse;
import com.github.aadvorak.artilleryonline.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomEndpoint {

    private final RoomService roomService;

    @GetMapping
    public RoomResponse getRoom() {
        return roomService.getRoom();
    }

    @PutMapping
    public RoomResponse createRoom() {
        return roomService.createRoom();
    }

    @PutMapping("/select-vehicle")
    public void selectVehicle(@RequestBody BattleParticipantParams request) {
        roomService.selectVehicle(request);
    }

    @PostMapping("/start-battle")
    public void startBattle() {
        roomService.startBattle();
    }

    @DeleteMapping("/exit")
    public void exitRoom() {
        roomService.exitRoom();
    }

    @DeleteMapping("/guests/{nickname}")
    public void removeUserFromRoom(@PathVariable String nickname) {
        roomService.removeUserFromRoom(nickname);
    }
}
