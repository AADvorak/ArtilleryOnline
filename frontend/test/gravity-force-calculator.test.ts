import {VehicleUtils} from "~/playground/utils/vehicle-utils";
import type {BattleModel, RoomModel, VehicleModel} from "~/playground/data/model.ts";
import {test} from "@jest/globals";
import {expect} from "@jest/globals";
import vehicle from "./vehicle-model-medium.json";
import room from "./room-model.json"
import {GravityForceCalculator} from "~/playground/battle/calculator/common/gravity-force-calculator";
import {BodyUtils} from "~/playground/utils/body-utils";
import {Constants} from "~/playground/data/constants";
import {BattleUtils} from "~/playground/utils/battle-utils";

const GROUND_LEVEL = 1.0
const SMALL_DELTA = 0.00001

const gravityForceCalculator = new GravityForceCalculator()

test('full under ground', () => {
  // @ts-ignore
  const vehicleModel = vehicle as VehicleModel
  // @ts-ignore
  const roomModel = room as RoomModel
  const battleModel = {room: roomModel} as BattleModel
  vehicleModel.state.position.x = 10.0
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  VehicleUtils.calculateAllGroundContacts(calculations, roomModel)
  const gravityForces = gravityForceCalculator.calculate(calculations, battleModel)
  expect(gravityForces.length).toBe(0)
  expect(BodyUtils.getAllGroundContacts(calculations).length > 2).toBe(true)
})

test('wheels in ground', () => {
  // @ts-ignore
  const vehicleModel = vehicle as VehicleModel
  // @ts-ignore
  const roomModel = room as RoomModel
  const battleModel = {room: roomModel} as BattleModel
  vehicleModel.state.position.x = 10.0
  vehicleModel.state.position.y = GROUND_LEVEL + 2 * vehicleModel.specs.wheelRadius
      - SMALL_DELTA - Constants.INTERPENETRATION_THRESHOLD
  BattleUtils.shiftPosition(vehicleModel.state.position, vehicleModel.preCalc.centerOfMassShift)
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  VehicleUtils.calculateAllGroundContacts(calculations, roomModel)
  const gravityForces = gravityForceCalculator.calculate(calculations, battleModel)
  expect(gravityForces.length).toBe(0)
  expect(BodyUtils.getAllGroundContacts(calculations).length).toBe(2)
})

test('full over ground', () => {
  // @ts-ignore
  const vehicleModel = vehicle as VehicleModel
  // @ts-ignore
  const roomModel = room as RoomModel
  const battleModel = {room: roomModel} as BattleModel
  vehicleModel.state.position.x = 10.0
  vehicleModel.state.position.y = GROUND_LEVEL + 2 * vehicleModel.specs.wheelRadius
      + SMALL_DELTA - Constants.INTERPENETRATION_THRESHOLD
  BattleUtils.shiftPosition(vehicleModel.state.position, vehicleModel.preCalc.centerOfMassShift)
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  VehicleUtils.calculateAllGroundContacts(calculations, roomModel)
  const gravityForces = gravityForceCalculator.calculate(calculations, battleModel)
  expect(gravityForces.length).toBe(1)
  expect(BodyUtils.getAllGroundContacts(calculations).length).toBe(0)
  const gravityForce = gravityForces[0]!
  expect(gravityForce.moving?.x).toBeCloseTo(0, 3)
  expect(gravityForce.moving?.y).toBeCloseTo(- vehicleModel.preCalc.mass * roomModel.specs.gravityAcceleration, 3)
  expect(gravityForce.radiusVector).toBeNull()
  expect(gravityForce.rotating).toBeNull()
})

test('one wheel over ground', () => {
  // @ts-ignore
  const vehicleModel = vehicle as VehicleModel
  // @ts-ignore
  const roomModel = room as RoomModel
  const battleModel = {room: roomModel} as BattleModel
  vehicleModel.state.position.x = 10.0
  vehicleModel.state.position.y = GROUND_LEVEL + 2 * vehicleModel.specs.wheelRadius
      - Constants.INTERPENETRATION_THRESHOLD
  vehicleModel.state.position.angle = Math.PI / 16
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  VehicleUtils.calculateAllGroundContacts(calculations, roomModel)
  const gravityForces = gravityForceCalculator.calculate(calculations, battleModel)
  expect(gravityForces.length).toBe(1)
  expect(BodyUtils.getAllGroundContacts(calculations).length).toBe(1)
  const gravityForce = gravityForces[0]!
  expect(gravityForce.moving?.x).toBeCloseTo(0, 3)
  expect(gravityForce.moving?.y).toBeCloseTo(- vehicleModel.preCalc.mass * roomModel.specs.gravityAcceleration, 3)
  expect(gravityForce.radiusVector).toBeNull()
  expect(gravityForce.rotating).toBeNull()
})
