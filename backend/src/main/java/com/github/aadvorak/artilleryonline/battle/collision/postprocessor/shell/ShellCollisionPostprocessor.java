package com.github.aadvorak.artilleryonline.battle.collision.postprocessor.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.postprocessor.CollisionPostprocessor;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.ShellHitType;
import com.github.aadvorak.artilleryonline.battle.events.ShellHitEvent;
import com.github.aadvorak.artilleryonline.battle.events.ShellHitEventObject;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.dto.response.ContactResponse;
import org.springframework.stereotype.Component;

@Component
public class ShellCollisionPostprocessor implements CollisionPostprocessor {

    @Override
    public void process(Calculations<?> calculations, BattleCalculations battle) {
        if (calculations instanceof ShellCalculations shell) {
            for (var collision : shell.getAllCollisions()) {
                if (collision.isHit()) {
                    battle.getModel().getUpdates().removeShell(shell.getId());
                    var hitType = ShellHitType.of(collision.getPair().second());
                    if (hitType != null) {
                        addHitEvent(collision.getContact(), hitType, shell.getId(),
                                collision.getSecondId(), battle.getModel());
                    }
                    return;
                }
            }
        }
    }

    private void addHitEvent(
            Contact contact,
            ShellHitType type,
            Integer shellId,
            Integer id,
            BattleModel battleModel
    ) {
        battleModel.getEvents().addHit(new ShellHitEvent()
                .setShellId(shellId)
                .setContact(ContactResponse.of(contact))
                .setObject(new ShellHitEventObject()
                        .setId(id)
                        .setType(type)));
    }
}
