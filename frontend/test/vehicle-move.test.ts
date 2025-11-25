import {expect, test} from "@jest/globals";
import vehicleMoveTestBattle from "./vehicle-move-test-battle.json";
import {useBattleObjectsProcessor} from "~/playground/battle/processor/battle-objects-processor";
import type {Battle} from "~/playground/data/battle";

const TIME_STEP = 0.01

const processorWithoutCollision = useBattleObjectsProcessor(false, false, 0)

test("vehicle-move-right-no-collisions", () => {
  // @ts-ignore
  const battle = vehicleMoveTestBattle as Battle
  const vehicleModel = battle.model.vehicles['test']!
  for (let i = 0; i < 100; i++) {
    processorWithoutCollision.process(battle, TIME_STEP)
  }
  const position = vehicleModel.state.position
  expect(position.x).toBeCloseTo(4.463556, 5)
  expect(position.y).toBeCloseTo(1.135073, 5)
  expect(position.angle).toBeCloseTo(0.218668, 5)
})
