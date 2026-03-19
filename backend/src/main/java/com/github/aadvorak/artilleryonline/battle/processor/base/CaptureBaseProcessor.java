package com.github.aadvorak.artilleryonline.battle.processor.base;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BaseModel;
import com.github.aadvorak.artilleryonline.battle.processor.AfterStep1Processor;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CaptureBaseProcessor implements AfterStep1Processor {

    @Override
    public void process(BattleCalculations battle) {
        battle.getModel().getBases().values().forEach(base -> processBase(base, battle));
    }

    private void processBase(BaseModel baseModel, BattleCalculations battle) {
        if (baseModel.getState().isCaptured()) {
            return;
        }
        var nicknamesOnBase = battle.getVehicles().stream()
                .filter(vehicle -> Math.abs(baseModel.getConfig().getPositionX()
                        - vehicle.getPosition().getX()) < baseModel.getSpecs().getRadius())
                .filter(vehicle -> {
                    var groundPosition = BattleUtils.getNearestGroundPosition(vehicle.getPosition().getX(), battle.getModel().getRoom());
                    return vehicle.getPosition().getY() - groundPosition.getY() < 2 * vehicle.getModel().getPreCalc().getMaxRadius();
                })
                .map(vehicle -> vehicle.getModel().getNickname())
                .collect(Collectors.toSet());
        baseModel.removeNotOnBaseCapturePoints(nicknamesOnBase);
        baseModel.addOnBaseCapturePoints(nicknamesOnBase);
        var teamIdsOnBase = nicknamesOnBase.stream()
                .map(nickname -> battle.getNicknameTeamMap().get(nickname))
                .collect(Collectors.toSet());
        if (teamIdsOnBase.isEmpty() && baseModel.getState().getCapturingTeamId() != null) {
            baseModel.setCapturingTeamId(null);
        } else if (teamIdsOnBase.size() == 1) {
            var teamId = teamIdsOnBase.iterator().next();
            if (!teamId.equals(baseModel.getState().getCapturingTeamId())) {
                baseModel.setCapturingTeamId(teamId);
            }
            baseModel.increaseCapturePoints(battle.getModel().getCurrentTimeStepSecs());
            baseModel.setCapturedIfEnoughPoints();
            baseModel.unblockCapture();
        } else if (teamIdsOnBase.size() > 1) {
            baseModel.blockCapture();
        }
    }
}
