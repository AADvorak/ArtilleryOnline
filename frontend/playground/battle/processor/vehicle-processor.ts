import type { BattleModel, VehicleModel } from '@/playground/data/model'
import { MovingDirection } from '@/playground/data/common'
import { VehicleAccelerationCalculator } from '@/playground/battle/calculator/vehicle-acceleration-calculator'
import { type VehicleCalculations, type WheelCalculations, WheelSign } from '@/playground/data/calculations'
import {VehicleUtils} from "~/playground/utils/vehicle-utils";

export const VehicleProcessor = {
  processStep(vehicleModel: VehicleModel, battleModel: BattleModel, timeStepSecs: number) {
    const calculations = this.initVehicleCalculations()
    this.recalculateVelocity(calculations, vehicleModel, battleModel, timeStepSecs)
    this.recalculatePositionAndAngle(vehicleModel, timeStepSecs)
    this.recalculateGunAngle(vehicleModel, timeStepSecs)
    if (vehicleModel.state.gunState.loadingShell) {
      vehicleModel.state.gunState.loadRemainTime -= timeStepSecs
    }
    if (vehicleModel.state.jetState && vehicleModel.state.jetState.volume < vehicleModel.config.jet.capacity) {
      vehicleModel.state.jetState.volume += (VehicleUtils.isJetActive(vehicleModel)
          ? - vehicleModel.config.jet.consumption
          : vehicleModel.config.jet.regeneration) * timeStepSecs
    }
  },

  initVehicleCalculations(): VehicleCalculations {
    return {
      nextPosition: undefined,
      rightWheel: this.initWheelCalculations(WheelSign.RIGHT),
      leftWheel: this.initWheelCalculations(WheelSign.LEFT)
    }
  },

  initWheelCalculations(sign: WheelSign): WheelCalculations {
    return {
      groundState: undefined,
      groundDepth: undefined,
      groundAngle: undefined,
      nearestGroundPoint: undefined,
      nearestGroundPointByX: undefined,
      position: undefined,
      sumAcceleration: undefined,
      velocity: undefined,
      sign,
      gravityAcceleration: { x: 0, y: 0 },
      engineAcceleration: { x: 0, y: 0 },
      groundReactionAcceleration: { x: 0, y: 0 },
      groundFrictionAcceleration: { x: 0, y: 0 },
      jetAcceleration: { x: 0, y: 0 }
    }
  },

  recalculateVelocity(
    calculations: VehicleCalculations,
    vehicleModel: VehicleModel,
    battleModel: BattleModel,
    timeStepSecs: number
  ) {
    const acceleration = VehicleAccelerationCalculator.getVehicleAcceleration(
      calculations,
      vehicleModel,
      battleModel.room
    )
    const velocity = vehicleModel.state.velocity
    velocity.x += acceleration.x * timeStepSecs
    velocity.y += acceleration.y * timeStepSecs
    velocity.angle += acceleration.angle * timeStepSecs
  },

  recalculatePositionAndAngle(vehicleModel: VehicleModel, timeStepSecs: number) {
    const velocity = vehicleModel.state.velocity
    const position = vehicleModel.state.position
    position.x += velocity.x * timeStepSecs
    position.y += velocity.y * timeStepSecs
    position.angle += velocity.angle * timeStepSecs
    if (position.angle > Math.PI / 2) {
      position.angle = Math.PI / 2
    }
    if (position.angle < - Math.PI / 2) {
      position.angle = - Math.PI / 2
    }
  },

  recalculateGunAngle(vehicleModel: VehicleModel, timeStepSecs: number) {
    const rotatingDirection = vehicleModel.state.gunRotatingDirection
    const rotatingVelocity = vehicleModel.config.gun.rotationVelocity
    const maxGunAngle = vehicleModel.specs.maxAngle
    const minGunAngle = vehicleModel.specs.minAngle
    const vehicleAngle = vehicleModel.state.position.angle
    let targetAngle = vehicleModel.state.gunState.targetAngle
    if (rotatingDirection) {
      const sign = MovingDirection.RIGHT === rotatingDirection ? -1 : 1
      targetAngle += sign * rotatingVelocity * timeStepSecs
    }
    targetAngle = this.restrictValue(targetAngle, minGunAngle + vehicleAngle, maxGunAngle + vehicleAngle)
    vehicleModel.state.gunState.targetAngle = targetAngle
    let gunAngle = vehicleModel.state.gunState.angle
    const angleDiff = targetAngle - vehicleAngle - gunAngle
    if (Math.abs(angleDiff) > rotatingVelocity * 0.01) {
      const angleStep = Math.sign(angleDiff) * rotatingVelocity * timeStepSecs
      gunAngle += Math.min(angleStep, angleDiff)
      gunAngle = this.restrictValue(gunAngle, minGunAngle, maxGunAngle)
      vehicleModel.state.gunState.angle = gunAngle
    }
  },

  restrictValue(value: number, min: number, max: number) {
    if (value < min) {
      return min
    }
    return Math.min(value, max)
  }
}
