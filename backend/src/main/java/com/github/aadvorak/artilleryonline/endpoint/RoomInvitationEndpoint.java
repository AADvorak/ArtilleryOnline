package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.dto.request.RoomInvitationRequest;
import com.github.aadvorak.artilleryonline.dto.response.RoomInvitationResponse;
import com.github.aadvorak.artilleryonline.dto.response.RoomResponse;
import com.github.aadvorak.artilleryonline.service.RoomInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms/invitations")
@RequiredArgsConstructor
public class RoomInvitationEndpoint {

    private final RoomInvitationService roomInvitationService;

    @PostMapping()
    public RoomInvitationResponse inviteToRoom(@RequestBody RoomInvitationRequest request) {
        return roomInvitationService.inviteToRoom(request);
    }

    @PostMapping("/{invitationId}/accept")
    public RoomResponse acceptInvitation(@PathVariable String invitationId) {
        return roomInvitationService.acceptInvitation(invitationId);
    }

    @DeleteMapping("/{invitationId}")
    public void deleteInvitation(@PathVariable String invitationId) {
        roomInvitationService.deleteInvitation(invitationId);
    }
}
