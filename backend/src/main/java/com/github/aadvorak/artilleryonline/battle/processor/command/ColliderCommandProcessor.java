package com.github.aadvorak.artilleryonline.battle.processor.command;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.command.Command;
import com.github.aadvorak.artilleryonline.battle.command.DebugCommand;
import com.github.aadvorak.artilleryonline.battle.model.BodyModel;

import java.util.Map;

public class ColliderCommandProcessor {

    public static void process(DebugCommand command, Battle battle) {
        var bodies = battle.getModel().getBodies();

        if (Command.STOP_ALL.equals(command.getCommand())) {
            stopAll(bodies);
            return;
        }

        if (Command.SWITCH_BODY.equals(command.getCommand())) {
            var id = battle.getDebug().getColliderObjectId() + 1;
            if (bodies.size() < id) {
                id = 1;
            }
            battle.getDebug().setColliderObjectId(id);
            return;
        }

        var body = bodies.get(battle.getDebug().getColliderObjectId());

        if (body == null) {
            return;
        }

        if (Command.START_PUSHING.equals(command.getCommand())) {
            var direction = command.getParams().getDirection();
            body.getState().setPushingDirection(direction);
            body.getUpdate().setUpdated();
        }

        if (Command.STOP_PUSHING.equals(command.getCommand())) {
            var direction = command.getParams().getDirection();
            var vehicleDirection = body.getState().getPushingDirection();
            if (direction != null && direction.equals(vehicleDirection)) {
                body.getState().setPushingDirection(null);
                body.getUpdate().setUpdated();
            }
        }

        if (Command.START_ROTATING.equals(command.getCommand())) {
            var direction = command.getParams().getDirection();
            body.getState().setRotatingDirection(direction);
            body.getUpdate().setUpdated();
        }

        if (Command.STOP_ROTATING.equals(command.getCommand())) {
            var direction = command.getParams().getDirection();
            var vehicleDirection = body.getState().getRotatingDirection();
            if (direction != null && direction.equals(vehicleDirection)) {
                body.getState().setRotatingDirection(null);
                body.getUpdate().setUpdated();
            }
        }
    }

    private static void stopAll(Map<Integer, BodyModel<?,?,?,?>> bodies) {
        bodies.values().forEach(body -> body.getState().getVelocity()
                .setX(0)
                .setY(0)
                .setAngle(0));
    }
}
