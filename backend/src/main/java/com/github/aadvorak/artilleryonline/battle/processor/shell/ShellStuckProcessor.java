package com.github.aadvorak.artilleryonline.battle.processor.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.processor.BeforeStep1Processor;
import org.springframework.stereotype.Component;

@Component
public class ShellStuckProcessor implements BeforeStep1Processor {

    @Override
    public void process(BattleCalculations battle) {
        battle.getShells().forEach(shell -> processShell(shell, battle.getModel()));
    }

    private void processShell(ShellCalculations shell, BattleModel battleModel) {
        if (shell.getModel().getState().isStuck()) {
            shell.getModel().getState().setStuckTime(shell.getModel().getState().getStuckTime()
                    + battleModel.getCurrentTimeStepSecs());
            if (shell.getModel().getState().getStuckTime() > 3.0) {
                battleModel.getUpdates().removeShell(shell.getId());
            }
        }
    }
}
