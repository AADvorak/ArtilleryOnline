package com.github.aadvorak.artilleryonline;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.collision.CollisionsProcessor;
import com.github.aadvorak.artilleryonline.battle.collision.detector.vehicle.VehicleGroundCollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.collision.postprocessor.vehicle.VehicleCollisionPostprocessor;
import com.github.aadvorak.artilleryonline.battle.collision.preprocessor.vehicle.VehicleCollisionPreprocessor;
import com.github.aadvorak.artilleryonline.battle.collision.resolver.CollisionResolver;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.processor.AllBattleObjectsProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleOnGroundProcessor;
import com.github.aadvorak.artilleryonline.battle.updates.BattleModelUpdates;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VehicleMoveTest {

    private static final double SMALL_DELTA = 0.00001;

    private final CollisionsProcessor emptyCollisionsProcessor = new CollisionsProcessor(
            new ApplicationSettings(),
            Set.of(),
            Set.of(),
            Set.of(),
            new CollisionResolver(new ApplicationSettings())
    );

    private final CollisionsProcessor collisionsProcessor = new CollisionsProcessor(
            new ApplicationSettings(),
            Set.of(new VehicleGroundCollisionsDetector()),
            Set.of(new VehicleCollisionPreprocessor()),
            Set.of(new VehicleCollisionPostprocessor()),
            new CollisionResolver(new ApplicationSettings())
    );

    private final AllBattleObjectsProcessor processorWithoutCollisions = new AllBattleObjectsProcessor(
            emptyCollisionsProcessor,
            List.of(), List.of(), List.of(), List.of()
    );

    private final AllBattleObjectsProcessor processor = new AllBattleObjectsProcessor(
            collisionsProcessor,
            List.of(), List.of(), List.of(), List.of()
    );

    @Test
    public void moveRightNoCollisions() {
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
        for (int i = 0; i < 100; i++) {
            processorWithoutCollisions.process(battle);
        }
        var position = vehicleModel.getState().getPosition();
        assertAll(
                () -> assertEquals(4.463556, position.getX(), SMALL_DELTA),
                () -> assertEquals(1.135073, position.getY(), SMALL_DELTA),
                () -> assertEquals(0.218668, position.getAngle(), SMALL_DELTA)
        );
    }
}
