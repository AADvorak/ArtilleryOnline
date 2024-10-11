import type { BattleModel, VehicleModel } from '@/playground/data/model'
import { MovingDirection } from '@/playground/data/common'
import { VehicleAccelerationCalculator } from '@/playground/battle/calculator/vehicle-acceleration-calculator'
import { type VehicleCalculations, type WheelCalculations, WheelSign } from '@/playground/data/calculations'

export const VehicleProcessor = {
  processStep(vehicleModel: VehicleModel, battleModel: BattleModel, timeStepSecs: number) {
    const calculations = this.initVehicleCalculations()
    this.recalculateVelocity(calculations, vehicleModel, battleModel, timeStepSecs)
    this.recalculatePositionAndAngle(vehicleModel, timeStepSecs)
    if (vehicleModel.state.gunRotatingDirection) {
      const sign = MovingDirection.RIGHT === vehicleModel.state.gunRotatingDirection ? -1 : 1
      vehicleModel.state.gunAngle += sign * vehicleModel.config.gun.rotationVelocity * timeStepSecs
    }
    if (vehicleModel.state.gunState.loadingShell) {
      vehicleModel.state.gunState.loadRemainTime -= timeStepSecs
    }
  },

  initVehicleCalculations(): VehicleCalculations {
    return {
      nextAngle: undefined,
      nextPosition: undefined,
      rightWheel: this.initWheelCalculations(WheelSign.RIGHT),
      leftWheel: this.initWheelCalculations(WheelSign.LEFT)
    }
  },

  initWheelCalculations(sign: WheelSign): WheelCalculations {
    return {
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
      groundFrictionAcceleration: { x: 0, y: 0 }
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
    let angle = vehicleModel.state.angle + velocity.angle * timeStepSecs
    if (angle > Math.PI / 2) {
      angle = Math.PI / 2
    }
    if (angle < - Math.PI / 2) {
      angle = - Math.PI / 2
    }
    vehicleModel.state.angle = angle
  }
}
