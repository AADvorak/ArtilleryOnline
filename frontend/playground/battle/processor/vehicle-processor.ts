import type { BattleModel, VehicleModel } from '@/playground/data/model'
import {MovingDirection, type Position} from '@/playground/data/common'
import { VehicleAccelerationCalculator } from '@/playground/battle/calculator/vehicle-acceleration-calculator'
import { type VehicleCalculations, type WheelCalculations, WheelSign } from '@/playground/data/calculations'
import {VehicleUtils} from "~/playground/utils/vehicle-utils";

export const VehicleProcessor = {
  processStep(vehicleModel: VehicleModel, battleModel: BattleModel, timeStepSecs: number) {
    const calculations = this.initVehicleCalculations(vehicleModel)
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

  initVehicleCalculations(model: VehicleModel): VehicleCalculations {
    return {
      model,
      nextPosition: undefined,
      rightWheel: this.initWheelCalculations(WheelSign.RIGHT, VehicleUtils.getRightWheelPosition(model)),
      leftWheel: this.initWheelCalculations(WheelSign.LEFT, VehicleUtils.getLeftWheelPosition(model))
    }
  },

  initWheelCalculations(sign: WheelSign, position: Position): WheelCalculations {
    return {
      position,
      velocity: undefined,
      groundContact: null,
      sign,
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
      battleModel
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
  },

  recalculateGunAngle(vehicleModel: VehicleModel, timeStepSecs: number) {
    const gunState = vehicleModel.state.gunState
    const rotatingDirection = gunState.rotatingDirection
    const rotatingVelocity = vehicleModel.config.gun.rotationVelocity
    const maxGunAngle = vehicleModel.specs.maxAngle
    const minGunAngle = vehicleModel.specs.minAngle
    const vehicleAngle = vehicleModel.state.position.angle
    let targetAngle = gunState.fixed
        ? vehicleAngle + gunState.angle
        : gunState.targetAngle
    if (rotatingDirection) {
      const sign = MovingDirection.RIGHT === rotatingDirection ? -1 : 1
      targetAngle += sign * rotatingVelocity * timeStepSecs
    }
    targetAngle = this.restrictValue(targetAngle, minGunAngle + vehicleAngle, maxGunAngle + vehicleAngle)
    gunState.targetAngle = targetAngle
    if (gunState.fixed) {
      gunState.angle = targetAngle - vehicleAngle
    } else {
      let gunAngle = gunState.angle
      const angleDiff = targetAngle - vehicleAngle - gunAngle
      if (Math.abs(angleDiff) > rotatingVelocity * 0.01) {
        const angleStep = Math.sign(angleDiff) * rotatingVelocity * timeStepSecs
        gunAngle += Math.min(angleStep, angleDiff)
        gunAngle = this.restrictValue(gunAngle, minGunAngle, maxGunAngle)
        gunState.angle = gunAngle
      }
    }
  },

  restrictValue(value: number, min: number, max: number) {
    if (value < min) {
      return min
    }
    return Math.min(value, max)
  }
}
