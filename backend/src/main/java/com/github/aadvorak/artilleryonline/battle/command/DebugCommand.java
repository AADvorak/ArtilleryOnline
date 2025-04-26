package com.github.aadvorak.artilleryonline.battle.command;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DebugCommand {

    private Command command;

    private CommandParams params;
}
