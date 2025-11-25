import type {BattleModel, RoomModel, VehicleModel} from "~/playground/data/model.ts";
import {expect, test} from "@jest/globals";
import vehicle from "./vehicle-model-medium.json";
import room from "./room-model.json"
import {Constants} from "~/playground/data/constants";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {
  WheelsGroundReactionForceCalculator
} from "~/playground/battle/calculator/vehicle/wheels-ground-reaction-force-calculator";
import {VehicleCalculations} from "~/playground/data/calculations";

const GROUND_LEVEL = 1.0
const SMALL_DELTA = 0.00001

const calculator = new WheelsGroundReactionForceCalculator()

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
  const calculations = new VehicleCalculations(vehicleModel)
  calculations.calculateAllGroundContacts(roomModel)
  const forces = calculator.calculate(calculations, battleModel)
  expect(forces.length).toBe(0)
  expect(calculations.getGroundContacts().size).toBe(2)
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
  const calculations = new VehicleCalculations(vehicleModel)
  calculations.calculateAllGroundContacts(roomModel)
  const forces = calculator.calculate(calculations, battleModel)
  expect(forces.length).toBe(0)
  expect(calculations.getGroundContacts().size).toBe(2)
})

test('wheels in ground moving up', () => {
  // @ts-ignore
  const vehicleModel = JSON.parse(JSON.stringify(vehicle)) as VehicleModel
  // @ts-ignore
  const roomModel = room as RoomModel
  const battleModel = {room: roomModel} as BattleModel
  vehicleModel.state.position.x = 10.0
  vehicleModel.state.velocity.y = 1.0
  vehicleModel.state.position.y = GROUND_LEVEL + 2 * vehicleModel.specs.wheelRadius
      - SMALL_DELTA - Constants.INTERPENETRATION_THRESHOLD
  BattleUtils.shiftPosition(vehicleModel.state.position, vehicleModel.preCalc.centerOfMassShift)
  const calculations = new VehicleCalculations(vehicleModel)
  calculations.calculateAllGroundContacts(roomModel)
  const forces = calculator.calculate(calculations, battleModel)
  expect(forces.length).toBe(0)
  expect(calculations.getGroundContacts().size).toBe(2)
})

test('wheels in ground moving down', () => {
  // @ts-ignore
  const vehicleModel = JSON.parse(JSON.stringify(vehicle)) as VehicleModel
  // @ts-ignore
  const roomModel = room as RoomModel
  const battleModel = {room: roomModel} as BattleModel
  vehicleModel.state.position.x = 10.0
  vehicleModel.state.velocity.y = -1.0
  vehicleModel.state.position.y = GROUND_LEVEL + 2 * vehicleModel.specs.wheelRadius
      - SMALL_DELTA - Constants.INTERPENETRATION_THRESHOLD
  BattleUtils.shiftPosition(vehicleModel.state.position, vehicleModel.preCalc.centerOfMassShift)
  const calculations = new VehicleCalculations(vehicleModel)
  calculations.calculateAllGroundContacts(roomModel)
  const forces = calculator.calculate(calculations, battleModel)
  const sumForces = forces.map(force => VectorUtils.sumOf(force.moving!, force.rotating!))
  const expectedDepth = SMALL_DELTA + Constants.INTERPENETRATION_THRESHOLD
  const expectedY = expectedDepth * roomModel.specs.groundReactionCoefficient
  const factY = sumForces.length ? sumForces[0]!.y : 0.0
  const factDepth = calculations.getGroundContacts().size
      ? calculations.getGroundContacts().values().next().value!.depth : 0.0
  expect(forces.length).toBe(2)
  expect(sumForces.filter(force => force.y > 0).length > 0).toBeTruthy()
  expect(sumForces.filter(force => force.y < 0).length > 0).toBeFalsy()
  expect(sumForces.filter(force => Math.abs(force.x) > Constants.ZERO_THRESHOLD).length > 0).toBeFalsy()
  expect(factDepth).toBeCloseTo(expectedDepth, SMALL_DELTA)
  expect(factY).toBeCloseTo(expectedY, SMALL_DELTA)
  expect(calculations.getGroundContacts().size).toBe(2)
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
  const calculations = new VehicleCalculations(vehicleModel)
  calculations.calculateAllGroundContacts(roomModel)
  const forces = calculator.calculate(calculations, battleModel)
  expect(forces.length).toBe(0)
  expect(calculations.getGroundContacts().size).toBe(0)
})

test('full over ground moving down', () => {
  // @ts-ignore
  const vehicleModel = JSON.parse(JSON.stringify(vehicle)) as VehicleModel
  // @ts-ignore
  const roomModel = room as RoomModel
  const battleModel = {room: roomModel} as BattleModel
  vehicleModel.state.position.x = 10.0
  vehicleModel.state.velocity.y = -1.0
  vehicleModel.state.position.y = GROUND_LEVEL + 2 * vehicleModel.specs.wheelRadius
      + SMALL_DELTA - Constants.INTERPENETRATION_THRESHOLD
  BattleUtils.shiftPosition(vehicleModel.state.position, vehicleModel.preCalc.centerOfMassShift)
  const calculations = new VehicleCalculations(vehicleModel)
  calculations.calculateAllGroundContacts(roomModel)
  const forces = calculator.calculate(calculations, battleModel)
  expect(forces.length).toBe(0)
  expect(calculations.getGroundContacts().size).toBe(0)
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
  const calculations = new VehicleCalculations(vehicleModel)
  calculations.calculateAllGroundContacts(roomModel)
  const forces = calculator.calculate(calculations, battleModel)
  expect(forces.length).toBe(0)
  expect(calculations.getGroundContacts().size).toBe(1)
})

test('one wheel over ground moving down', () => {
  // @ts-ignore
  const vehicleModel = JSON.parse(JSON.stringify(vehicle)) as VehicleModel
  // @ts-ignore
  const roomModel = room as RoomModel
  const battleModel = {room: roomModel} as BattleModel
  vehicleModel.state.position.x = 10.0
  vehicleModel.state.velocity.y = -1.0
  vehicleModel.state.position.y = GROUND_LEVEL + 2 * vehicleModel.specs.wheelRadius
      - Constants.INTERPENETRATION_THRESHOLD
  vehicleModel.state.position.angle = Math.PI / 16
  const calculations = new VehicleCalculations(vehicleModel)
  calculations.calculateAllGroundContacts(roomModel)
  const forces = calculator.calculate(calculations, battleModel)
  const sumForces = forces.map(force => VectorUtils.sumOf(force.moving!, force.rotating!))
  expect(forces.length).toBe(1)
  expect(sumForces.filter(force => force.y > 0).length > 0).toBeTruthy()
  expect(sumForces.filter(force => force.y < 0).length > 0).toBeFalsy()
  expect(sumForces.filter(force => Math.abs(force.x) > Constants.ZERO_THRESHOLD).length > 0).toBeFalsy()
  expect(calculations.getGroundContacts().size).toBe(1)
})
