import {VehicleUtils} from "~/playground/utils/vehicle-utils";
import type {BattleModel, RoomModel, VehicleModel} from "~/playground/data/model.ts";
import {test} from "@jest/globals";
import {expect} from "@jest/globals";
import vehicle from "./vehicle-model-medium.json";
import room from "./room-model.json"
import {BodyUtils} from "~/playground/utils/body-utils";
import {Constants} from "~/playground/data/constants";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {
  WheelsGroundFrictionForceCalculator
} from "~/playground/battle/calculator/vehicle/wheels-ground-friction-force-calculator";
import {VectorUtils} from "~/playground/utils/vector-utils";

const GROUND_LEVEL = 1.0
const SMALL_DELTA = 0.00001

const calculator = new WheelsGroundFrictionForceCalculator()

test('wheels in ground no move', () => {
  // @ts-ignore
  const vehicleModel = JSON.parse(JSON.stringify(vehicle)) as VehicleModel
  // @ts-ignore
  const roomModel = room as RoomModel
  const battleModel = {room: roomModel} as BattleModel
  vehicleModel.state.position.x = 10.0
  vehicleModel.state.position.y = GROUND_LEVEL + 2 * vehicleModel.specs.wheelRadius
      - SMALL_DELTA - Constants.INTERPENETRATION_THRESHOLD
  BattleUtils.shiftPosition(vehicleModel.state.position, vehicleModel.preCalc.centerOfMassShift)
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  VehicleUtils.calculateAllGroundContacts(calculations, roomModel)
  const forces = calculator.calculate(calculations, battleModel)
  expect(forces.length).toBe(0)
  expect(BodyUtils.getAllGroundContacts(calculations).length).toBe(2)
})

test('wheels in ground moving x', () => {
  // @ts-ignore
  const vehicleModel = JSON.parse(JSON.stringify(vehicle)) as VehicleModel
  // @ts-ignore
  const roomModel = room as RoomModel
  const battleModel = {room: roomModel} as BattleModel
  vehicleModel.state.position.x = 10.0
  vehicleModel.state.velocity.x = 1.0
  vehicleModel.state.position.y = GROUND_LEVEL + 2 * vehicleModel.specs.wheelRadius
      - SMALL_DELTA - Constants.INTERPENETRATION_THRESHOLD
  BattleUtils.shiftPosition(vehicleModel.state.position, vehicleModel.preCalc.centerOfMassShift)
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  VehicleUtils.calculateAllGroundContacts(calculations, roomModel)
  const forces = calculator.calculate(calculations, battleModel)
  const sumForces = forces.map(force => VectorUtils.sumOf(force.moving!, force.rotating!))
  expect(forces.length).toBe(2)
  expect(sumForces.filter(force => force.x > 0).length > 0).toBeFalsy()
  expect(sumForces.filter(force => force.x < 0).length > 0).toBeTruthy()
  expect(sumForces.filter(force => Math.abs(force.y) > 0).length > 0).toBeFalsy()
  expect(BodyUtils.getAllGroundContacts(calculations).length).toBe(2)
})


test('full over ground no move', () => {
  // @ts-ignore
  const vehicleModel = JSON.parse(JSON.stringify(vehicle)) as VehicleModel
  // @ts-ignore
  const roomModel = room as RoomModel
  const battleModel = {room: roomModel} as BattleModel
  vehicleModel.state.position.x = 10.0
  vehicleModel.state.position.y = GROUND_LEVEL + 2 * vehicleModel.specs.wheelRadius
      + SMALL_DELTA - Constants.INTERPENETRATION_THRESHOLD
  BattleUtils.shiftPosition(vehicleModel.state.position, vehicleModel.preCalc.centerOfMassShift)
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  VehicleUtils.calculateAllGroundContacts(calculations, roomModel)
  const forces = calculator.calculate(calculations, battleModel)
  expect(forces.length).toBe(0)
  expect(BodyUtils.getAllGroundContacts(calculations).length).toBe(0)
})

test('full over ground moving x', () => {
  // @ts-ignore
  const vehicleModel = JSON.parse(JSON.stringify(vehicle)) as VehicleModel
  // @ts-ignore
  const roomModel = room as RoomModel
  const battleModel = {room: roomModel} as BattleModel
  vehicleModel.state.position.x = 10.0
  vehicleModel.state.velocity.x = 1.0
  vehicleModel.state.position.y = GROUND_LEVEL + 2 * vehicleModel.specs.wheelRadius
      + SMALL_DELTA - Constants.INTERPENETRATION_THRESHOLD
  BattleUtils.shiftPosition(vehicleModel.state.position, vehicleModel.preCalc.centerOfMassShift)
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  VehicleUtils.calculateAllGroundContacts(calculations, roomModel)
  const forces = calculator.calculate(calculations, battleModel)
  expect(forces.length).toBe(0)
  expect(BodyUtils.getAllGroundContacts(calculations).length).toBe(0)
})

test('one wheel over ground no move', () => {
  // @ts-ignore
  const vehicleModel = JSON.parse(JSON.stringify(vehicle)) as VehicleModel
  // @ts-ignore
  const roomModel = room as RoomModel
  const battleModel = {room: roomModel} as BattleModel
  vehicleModel.state.position.x = 10.0
  vehicleModel.state.position.y = GROUND_LEVEL + 2 * vehicleModel.specs.wheelRadius
      - Constants.INTERPENETRATION_THRESHOLD
  vehicleModel.state.position.angle = Math.PI / 16
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  VehicleUtils.calculateAllGroundContacts(calculations, roomModel)
  const forces = calculator.calculate(calculations, battleModel)
  expect(forces.length).toBe(0)
  expect(BodyUtils.getAllGroundContacts(calculations).length).toBe(1)
})

test('one wheel over ground moving x', () => {
  // @ts-ignore
  const vehicleModel = JSON.parse(JSON.stringify(vehicle)) as VehicleModel
  // @ts-ignore
  const roomModel = room as RoomModel
  const battleModel = {room: roomModel} as BattleModel
  vehicleModel.state.position.x = 10.0
  vehicleModel.state.velocity.x = 1.0
  vehicleModel.state.position.y = GROUND_LEVEL + 2 * vehicleModel.specs.wheelRadius
      - Constants.INTERPENETRATION_THRESHOLD
  vehicleModel.state.position.angle = Math.PI / 16
  const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
  VehicleUtils.calculateAllGroundContacts(calculations, roomModel)
  const forces = calculator.calculate(calculations, battleModel)
  const sumForces = forces.map(force => VectorUtils.sumOf(force.moving!, force.rotating!))
  expect(forces.length).toBe(1)
  expect(sumForces.filter(force => force.x > 0).length > 0).toBeFalsy()
  expect(sumForces.filter(force => force.x < 0).length > 0).toBeTruthy()
  expect(sumForces.filter(force => Math.abs(force.y) > 0).length > 0).toBeFalsy()
  expect(BodyUtils.getAllGroundContacts(calculations).length).toBe(1)
})
