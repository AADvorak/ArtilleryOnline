import type {BattleModel, BoxModel} from "~/playground/data/model.ts";
import {expect, test} from "@jest/globals";
import battle from "./battle-model.json";
import {BoxProcessor} from "~/playground/battle/processor/box-processor";
import {VectorUtils} from "~/playground/utils/vector-utils";

test('static box', () => {
  // @ts-ignore
  const battleModel = battle.model as BattleModel
  const boxModel = Object.values(battleModel.boxes)[0] as BoxModel
  BoxProcessor.processStep(boxModel, battleModel, 0.01)
  const velocity = boxModel.state.velocity
  const velocityMagnitude = VectorUtils.getBodyMagnitude(velocity)
  expect(velocityMagnitude).toBeCloseTo(0, 4)
})
