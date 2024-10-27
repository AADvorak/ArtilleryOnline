package com.github.aadvorak.artilleryonline.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EnterRoomRequest {

    private String invitationId;
}
