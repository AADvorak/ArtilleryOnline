package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.battle.BattleParticipantParams;
import com.github.aadvorak.artilleryonline.dto.request.BattleTypeRequest;
import com.github.aadvorak.artilleryonline.dto.request.BooleanRequest;
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

    @DeleteMapping("/my/members/{nickname}")
    public void removeMember(@PathVariable String nickname) {
        roomService.removeMember(nickname);
    }

    @PutMapping("/my/members/{nickname}/change-team/{teamId}")
    public void changeMembersTeam(@PathVariable String nickname, @PathVariable int teamId) {
        roomService.changeMembersTeam(nickname, teamId);
    }

    @PostMapping("/my/bots")
    public void addBot() {
        roomService.addBot();
    }

    @PutMapping("/my/open")
    public void changeOpen(@RequestBody BooleanRequest request) {
        roomService.changeOpen(request.isOn());
    }

    @PutMapping("/my/battle-type")
    public void changeBattleType(@RequestBody BattleTypeRequest request) {
        roomService.changeBattleType(request.getBattleType());
    }
}
