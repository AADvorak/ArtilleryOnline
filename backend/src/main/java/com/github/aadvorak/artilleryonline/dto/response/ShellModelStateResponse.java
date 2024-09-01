package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.state.ShellState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShellModelStateResponse {

    private int id;

    private ShellState state;
}
