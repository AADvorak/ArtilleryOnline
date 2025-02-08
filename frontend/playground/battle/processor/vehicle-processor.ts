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
    if (vehicleModel.state.gunRotatingDirection) {
      const sign = MovingDirection.RIGHT === vehicleModel.state.gunRotatingDirection ? -1 : 1
      let newAngle = vehicleModel.state.gunAngle + sign * vehicleModel.config.gun.rotationVelocity * timeStepSecs
      const minAngle = vehicleModel.specs.minAngle
      const maxAngle = vehicleModel.specs.maxAngle
      if (newAngle > maxAngle) {
        newAngle = maxAngle
      }
      if (newAngle < minAngle) {
        newAngle = minAngle
      }
      vehicleModel.state.gunAngle = newAngle
    }
  }
}
