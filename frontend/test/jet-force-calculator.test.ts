import {VehicleUtils} from "~/playground/utils/vehicle-utils";
import type {BattleModel, VehicleModel} from "~/playground/data/model.ts";
import {expect, test} from "@jest/globals";
import mediumModel from "./vehicle-model-medium.json";
import lightModel from "./vehicle-model-light.json";
import {MovingDirection} from "~/playground/data/common";
import {JetForceCalculator} from "~/playground/battle/calculator/vehicle/jet-force-calculator";
import {VectorUtils} from "~/playground/utils/vector-utils";

const SQRT_05 = Math.sqrt(0.5)

const jetForceCalculator = new JetForceCalculator()

test('horizontal off moving right', () => {
  // @ts-ignore
  const vehicleModel = mediumModel as VehicleModel
  const battleModel = {} as BattleModel
  vehicleModel.state.movingDirection = MovingDirection.RIGHT
  vehicleModel.state.jetState.active = false
  vehicleModel.state.jetState.volume = vehicleModel.config.jet.capacity
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  const jetForces = jetForceCalculator.calculate(calculations, battleModel)
  expect(jetForces.length).toBe(0)
})

test('horizontal on no fuel', () => {
  // @ts-ignore
  const vehicleModel = mediumModel as VehicleModel
  const battleModel = {} as BattleModel
  vehicleModel.state.movingDirection = MovingDirection.RIGHT
  vehicleModel.state.jetState.active = true
  vehicleModel.state.jetState.volume = 0
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  const jetForces = jetForceCalculator.calculate(calculations, battleModel)
  expect(jetForces.length).toBe(0)
})

test('horizontal on no moving direction', () => {
  // @ts-ignore
  const vehicleModel = mediumModel as VehicleModel
  const battleModel = {} as BattleModel
  vehicleModel.state.movingDirection = undefined
  vehicleModel.state.jetState.active = true
  vehicleModel.state.jetState.volume = vehicleModel.config.jet.capacity
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  const jetForces = jetForceCalculator.calculate(calculations, battleModel)
  expect(jetForces.length).toBe(0)
})

test('horizontal on moving right', () => {
  // @ts-ignore
  const vehicleModel = mediumModel as VehicleModel
  const battleModel = {} as BattleModel
  vehicleModel.state.jetState.active = true
  vehicleModel.state.movingDirection = MovingDirection.RIGHT
  vehicleModel.state.jetState.volume = vehicleModel.config.jet.capacity
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  const jetForces = jetForceCalculator.calculate(calculations, battleModel)
  expect(jetForces.length).toBe(1)
  const jetForce = jetForces[0]
  const angle = JetForceCalculator.HORIZONTAL_JET_ANGLE
  const magnitude = vehicleModel.config.jet.acceleration * vehicleModel.preCalc.mass
  expect(jetForce.moving?.x).toBeCloseTo(magnitude * Math.cos(angle), 3)
  expect(jetForce.moving?.y).toBeCloseTo(magnitude * Math.sin(angle), 3)
  expect(jetForce.rotating).toBeNull()
  expect(jetForce.radiusVector).toBeNull()
})

test('horizontal on moving left', () => {
  // @ts-ignore
  const vehicleModel = mediumModel as VehicleModel
  const battleModel = {} as BattleModel
  vehicleModel.state.jetState.active = true
  vehicleModel.state.movingDirection = MovingDirection.LEFT
  vehicleModel.state.jetState.volume = vehicleModel.config.jet.capacity
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  const jetForces = jetForceCalculator.calculate(calculations, battleModel)
  expect(jetForces.length).toBe(1)
  const jetForce = jetForces[0]
  const angle = JetForceCalculator.HORIZONTAL_JET_ANGLE
  const magnitude = vehicleModel.config.jet.acceleration * vehicleModel.preCalc.mass
  expect(jetForce.moving?.x).toBeCloseTo(-magnitude * Math.cos(angle), 3)
  expect(jetForce.moving?.y).toBeCloseTo(magnitude * Math.sin(angle), 3)
  expect(jetForce.rotating).toBeNull()
  expect(jetForce.radiusVector).toBeNull()
})

test('vertical on no moving direction', () => {
  // @ts-ignore
  const vehicleModel = lightModel as VehicleModel
  const battleModel = {} as BattleModel
  vehicleModel.state.movingDirection = undefined
  vehicleModel.state.jetState.active = true
  vehicleModel.state.jetState.volume = vehicleModel.config.jet.capacity
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  const jetForces = jetForceCalculator.calculate(calculations, battleModel)
  expect(jetForces.length).toBe(2)
  const jetForce1 = jetForces[0]
  const jetForce2 = jetForces[1]
  const magnitude = vehicleModel.config.jet.acceleration * vehicleModel.preCalc.mass
  const sumForce = VectorUtils.sumOf(jetForce1.moving!, jetForce1.rotating!, jetForce2.moving!, jetForce2.rotating!)
  expect(sumForce.x).toBeCloseTo(0, 3)
  expect(sumForce.y).toBeCloseTo(magnitude, 3)
  expect(VectorUtils.getMagnitude(jetForce1.radiusVector!)).toBeCloseTo(vehicleModel.preCalc.wheelDistance)
  expect(VectorUtils.getMagnitude(jetForce2.radiusVector!)).toBeCloseTo(vehicleModel.preCalc.wheelDistance)
})

test('vertical on moving right', () => {
  // @ts-ignore
  const vehicleModel = lightModel as VehicleModel
  const battleModel = {} as BattleModel
  vehicleModel.state.movingDirection = MovingDirection.RIGHT
  vehicleModel.state.jetState.active = true
  vehicleModel.state.jetState.volume = vehicleModel.config.jet.capacity
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  const jetForces = jetForceCalculator.calculate(calculations, battleModel)
  expect(jetForces.length).toBe(2)
  const jetForce1 = jetForces[0]
  const jetForce2 = jetForces[1]
  const magnitude = vehicleModel.config.jet.acceleration * vehicleModel.preCalc.mass
  const sumForce = VectorUtils.sumOf(jetForce1.moving!, jetForce1.rotating!, jetForce2.moving!, jetForce2.rotating!)
  expect(sumForce.x).toBeCloseTo(magnitude * SQRT_05, 3)
  expect(sumForce.y).toBeCloseTo(magnitude * SQRT_05, 3)
  expect(VectorUtils.getMagnitude(jetForce1.radiusVector!)).toBeCloseTo(vehicleModel.preCalc.wheelDistance)
  expect(VectorUtils.getMagnitude(jetForce2.radiusVector!)).toBeCloseTo(vehicleModel.preCalc.wheelDistance)
})

test('vertical on moving left', () => {
  // @ts-ignore
  const vehicleModel = lightModel as VehicleModel
  const battleModel = {} as BattleModel
  vehicleModel.state.movingDirection = MovingDirection.LEFT
  vehicleModel.state.jetState.active = true
  vehicleModel.state.jetState.volume = vehicleModel.config.jet.capacity
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  const jetForces = jetForceCalculator.calculate(calculations, battleModel)
  expect(jetForces.length).toBe(2)
  const jetForce1 = jetForces[0]
  const jetForce2 = jetForces[1]
  const magnitude = vehicleModel.config.jet.acceleration * vehicleModel.preCalc.mass
  const sumForce = VectorUtils.sumOf(jetForce1.moving!, jetForce1.rotating!, jetForce2.moving!, jetForce2.rotating!)
  expect(sumForce.x).toBeCloseTo(- magnitude * SQRT_05, 3)
  expect(sumForce.y).toBeCloseTo(magnitude * SQRT_05, 3)
  expect(VectorUtils.getMagnitude(jetForce1.radiusVector!)).toBeCloseTo(vehicleModel.preCalc.wheelDistance)
  expect(VectorUtils.getMagnitude(jetForce2.radiusVector!)).toBeCloseTo(vehicleModel.preCalc.wheelDistance)
})
