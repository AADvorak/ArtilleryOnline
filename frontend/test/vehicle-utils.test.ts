import {VehicleUtils} from "~/playground/utils/vehicle-utils";
import type {VehicleModel} from "~/playground/data/model.ts";
import {test} from "@jest/globals";
import {expect} from "@jest/globals";
import vehicleModel from "./vehicle-model.json";

test('wheel position', () => {
  // @ts-ignore
  const model = vehicleModel as VehicleModel
  const leftWheelPosition = VehicleUtils.getLeftWheelPosition(model)
  const rightWheelPosition = VehicleUtils.getRightWheelPosition(model)
  expect(rightWheelPosition.x).toBeCloseTo(0.4, 3)
  expect(rightWheelPosition.y).toBeCloseTo(-0.18035, 3)
  expect(leftWheelPosition.x).toBeCloseTo(-0.4, 3)
  expect(leftWheelPosition.y).toBeCloseTo(-0.18035, 3)
})
