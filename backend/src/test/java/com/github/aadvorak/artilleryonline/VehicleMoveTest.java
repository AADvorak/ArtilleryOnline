package com.github.aadvorak.artilleryonline;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.collision.CollisionsProcessor;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.collision.detector.vehicle.VehicleGroundCollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.collision.postprocessor.CollisionPostprocessor;
import com.github.aadvorak.artilleryonline.battle.collision.postprocessor.vehicle.VehicleCollisionPostprocessor;
import com.github.aadvorak.artilleryonline.battle.collision.preprocessor.CollisionPreprocessor;
import com.github.aadvorak.artilleryonline.battle.collision.preprocessor.vehicle.VehicleCollisionPreprocessor;
import com.github.aadvorak.artilleryonline.battle.collision.resolver.CollisionResolver;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.processor.AllBattleObjectsProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleOnGroundProcessor;
import com.github.aadvorak.artilleryonline.battle.updates.BattleModelUpdates;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VehicleMoveTest {

    private static final double SMALL_DELTA = 0.00001;

    @Test
    public void moveRightNoCollisions() {
        var vehicleAndBattle = generateVehicleAndBattle();
        for (int i = 0; i < 100; i++) {
            getProcessor(false, 0).process(vehicleAndBattle.battle);
        }
        var position = vehicleAndBattle.vehicle.getState().getPosition();
        assertAll(
                () -> assertEquals(4.463556, position.getX(), SMALL_DELTA),
                () -> assertEquals(1.135073, position.getY(), SMALL_DELTA),
                () -> assertEquals(0.218668, position.getAngle(), SMALL_DELTA)
        );
    }

    @Test
    public void moveRightWithCollisions() {
        var vehicleAndBattle = generateVehicleAndBattle();
        for (int i = 0; i < 100; i++) {
            System.out.println("-------- " + i + " --------");
            getProcessor(true, 0).process(vehicleAndBattle.battle);
            System.out.println(vehicleAndBattle.vehicle.getState().getPosition());
            System.out.println("---------------------------");
        }
        var position = vehicleAndBattle.vehicle.getState().getPosition();
        assertAll(
                () -> assertEquals(4.238195, position.getX(), SMALL_DELTA),
                () -> assertEquals(1.242146, position.getY(), SMALL_DELTA),
                () -> assertEquals(0.003954, position.getAngle(), SMALL_DELTA)
        );
    }

    private VehicleAndBattle generateVehicleAndBattle() {
        var vehicleModel = TestVehicleGenerator.generate("Medium");
        vehicleModel.getState().setMovingDirection(MovingDirection.RIGHT);
        vehicleModel.getState().getPosition().setX(2.0);
        var battleModel = new BattleModel()
                .setCurrentTimeStepSecs(0.01)
                .setRoom(TestRoomGenerator.generate())
                .setUpdates(new BattleModelUpdates())
                .setVehicles(Map.of("test", vehicleModel));
        var battle = new Battle().setModel(battleModel);
        VehicleOnGroundProcessor.estimateVehicleAngleByPosition(vehicleModel, battleModel.getRoom());
        VehicleOnGroundProcessor.correctVehiclePositionAndAngleOnGround(vehicleModel, battleModel.getRoom());
        return new VehicleAndBattle(vehicleModel, battle);
    }

    private AllBattleObjectsProcessor getProcessor(boolean detectCollisions, int additionalIterationsNumber) {
        var settings = new ApplicationSettings();
        settings.setDebug(true);
        settings.setAdditionalResolveCollisionsIterationsNumber(additionalIterationsNumber);
        var detectors = new HashSet<CollisionsDetector>();
        var preprocessors = new HashSet<CollisionPreprocessor>();
        var postprocessors = new HashSet<CollisionPostprocessor>();
        if (detectCollisions) {
            detectors.add(new VehicleGroundCollisionsDetector());
            preprocessors.add(new VehicleCollisionPreprocessor(settings));
            postprocessors.add(new VehicleCollisionPostprocessor());
        }
        var collisionsProcessor = new CollisionsProcessor(settings, detectors, preprocessors,
                postprocessors, new CollisionResolver(settings));
        return new AllBattleObjectsProcessor(collisionsProcessor,
                List.of(), List.of(), List.of(), List.of(), List.of());
    }

    private record VehicleAndBattle(VehicleModel vehicle, Battle battle) {}
}
