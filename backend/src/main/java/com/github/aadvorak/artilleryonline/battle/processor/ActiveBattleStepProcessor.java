package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.battle.BattleType;
import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.collision.CollisionsProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.box.BoxDropProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.command.CommandProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.drone.DroneLaunchProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.explosion.ExplosionInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ActiveBattleStepProcessor extends BattleStepProcessorBase implements BattleStepProcessor {

    private final CollisionsProcessor collisionsProcessor;

    private final List<BeforeStep1Processor> beforeStep1Processors;

    private final List<AfterStep1Processor> afterStep1Processors;

    private final List<BeforeStep2Processor> beforeStep2Processors;

    private final List<AfterStep2Processor> afterStep2Processors;

    @Override
    protected void doStepLogic(Battle battle) {
        var battleModel = battle.getModel();

        readCommandsFromQueue(battle);

        DroneLaunchProcessor.launch(battle);
        BoxDropProcessor.drop(battle);

        var battleCalculations = new BattleCalculations(battle);

        beforeStep1Processors.forEach(processor -> processor.process(battleCalculations));
        battleCalculations.getMovingObjects().forEach(movingObject -> {
            movingObject.recalculateVelocity(battleModel);
            movingObject.calculateNextPosition(battleModel.getCurrentTimeStepSecs());
        });
        afterStep1Processors.forEach(processor -> processor.process(battleCalculations));
        collisionsProcessor.process(battleCalculations);
        beforeStep2Processors.forEach(processor -> processor.process(battleCalculations));
        battleCalculations.getMovingObjects().forEach(Calculations::applyNextPosition);
        afterStep2Processors.forEach(processor -> processor.process(battleCalculations));

        if (battleModel.getUpdates().getRemoved() != null) {
            var removedExplosionIds = battleModel.getUpdates().getRemoved().getExplosionIds();
            if (removedExplosionIds != null) {
                removedExplosionIds.forEach(battleModel::removeExplosionById);
            }
            var removedShellIds = battleModel.getUpdates().getRemoved().getShellIds();
            if (removedShellIds != null) {
                removedShellIds.forEach(id -> {
                    var shellModel = battleModel.getShells().get(id);
                    ExplosionInitializer.init(shellModel.getState().getPosition(),
                            shellModel.getSpecs().getRadius(), battleModel);
                    battleModel.removeShellById(id);
                });
            }
            var removedMissileIds = battleModel.getUpdates().getRemoved().getMissileIds();
            if (removedMissileIds != null) {
                removedMissileIds.forEach(id -> {
                    var missileModel = battleModel.getMissiles().get(id);
                    if (missileModel != null) {
                        ExplosionInitializer.init(missileModel.getState().getPosition().getCenter(),
                                missileModel.getSpecs().getRadius(), battleModel);
                    }
                    battleModel.removeMissileById(id);
                });
            }
            var removedDroneIds = battleModel.getUpdates().getRemoved().getDroneIds();
            if (removedDroneIds != null) {
                removedDroneIds.forEach(id -> {
                    var droneModel = battleModel.getDrones().get(id);
                    if (droneModel != null && droneModel.getState().isDestroyed()) {
                        ExplosionInitializer.init(droneModel.getState().getPosition().getCenter(),
                                droneModel.getSpecs().getEnginesRadius(), battleModel);
                    }
                    battleModel.removeDroneById(id);
                });
            }
            var removedVehicleKeys = battleModel.getUpdates().getRemoved().getVehicleKeys();
            if (removedVehicleKeys != null) {
                removedVehicleKeys.forEach(key -> {
                    var vehicleModel = battleModel.getVehicles().get(key);
                    ExplosionInitializer.init(vehicleModel.getState().getPosition().getCenter(),
                            vehicleModel.getPreCalc().getMaxRadius(), battleModel);
                    battleModel.removeVehicleByKey(key);
                });
            }
        }
    }

    @Override
    protected boolean changeStageIfNeeded(Battle battle) {
        if (super.changeStageIfNeeded(battle)) {
            return true;
        }
        if (battle.getModel().getVehicles().isEmpty()
                && battle.getModel().getShells().isEmpty()
                && battle.getModel().getExplosions().isEmpty()
                && battle.getModel().getMissiles().isEmpty()) {
            battle.setStageAndResetTime(BattleStage.FINISHED);
            return true;
        }
        if (!BattleType.DRONE_HUNT.equals(battle.getType())
                && battle.getModel().getVehicles().size() == 1
                && battle.getModel().getShells().isEmpty()
                && battle.getModel().getExplosions().isEmpty()
                && battle.getModel().getMissiles().isEmpty()
                && battle.getModel().getDrones().isEmpty()) {
            battle.setStageAndResetTime(BattleStage.FINISHED);
            return true;
        }
        return false;
    }

    private void readCommandsFromQueue(Battle battle) {
        battle.getQueues().getUserCommandQueues().forEach((userId, commandQueue) -> {
            var commandsNumber = 10;
            while (commandsNumber > 0) {
                var userCommand = commandQueue.poll();
                if (userCommand == null) {
                    return;
                }
                CommandProcessor.process(battle.getUserMap().get(userId).getNickname(),
                        userCommand, battle.getModel());
                commandsNumber--;
            }
        });
    }
}
