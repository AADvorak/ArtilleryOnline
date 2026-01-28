package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.battle.BattleParticipantParams;
import com.github.aadvorak.artilleryonline.dto.request.RoomOpenRequest;
import com.github.aadvorak.artilleryonline.dto.response.RoomResponse;
import com.github.aadvorak.artilleryonline.dto.response.RoomShortResponse;
import com.github.aadvorak.artilleryonline.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomEndpoint {

    private final RoomService roomService;

    @GetMapping()
    public List<RoomShortResponse> getOpenRoom() {
        return roomService.getOpenRooms();
    }

    @PutMapping("/{id}/enter")
    public RoomResponse enterRoom(@PathVariable String id) {
        return roomService.enterRoom(id);
    }

    @GetMapping("/my")
    public RoomResponse getRoom() {
        return roomService.getRoom();
    }

    @PutMapping
    public RoomResponse createRoom() {
        return roomService.createRoom();
    }

    @PutMapping("/my/select-vehicle")
    public void selectVehicle(@RequestBody BattleParticipantParams request) {
        roomService.selectVehicle(request);
    }

    @PostMapping("/my/start-battle")
    public void startBattle() {
        roomService.startBattle();
    }

    @DeleteMapping("/my/exit")
    public void exitRoom() {
        roomService.exitRoom();
    }

    @DeleteMapping("/my/participants/{nickname}")
    public void removeParticipant(@PathVariable String nickname) {
        roomService.removeParticipant(nickname);
    }

    @PostMapping("/my/bots")
    public void addBot() {
        roomService.addBot();
    }

    @PutMapping("/my/opened")
    public void changeOpened(@RequestBody RoomOpenRequest request) {
        roomService.changeOpened(request.isOpened());
    }
}
