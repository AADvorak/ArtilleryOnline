import type { BattleModel, VehicleModel } from '@/data/model'
import { MovingDirection } from '@/data/common'
import {VehicleOnGroundProcessor} from "@/processor/vehicle-on-ground-processor";

export const VehicleProcessor = {
  processStep(vehicleModel: VehicleModel, battleModel: BattleModel, timeStepSecs: number) {
    if (vehicleModel.state.movingDirection) {
      vehicleModel.state.position = this.getNextVehiclePosition(vehicleModel, timeStepSecs)
      VehicleOnGroundProcessor.correctVehiclePositionAndAngleOnGround(vehicleModel, battleModel.room)
    }
    if (vehicleModel.state.gunRotatingDirection) {
      const sign = MovingDirection.RIGHT === vehicleModel.state.gunRotatingDirection ? -1 : 1
      vehicleModel.state.gunAngle += sign * vehicleModel.config.gun.rotationVelocity * timeStepSecs
    }
    if (vehicleModel.state.gunState.loadingShell) {
      vehicleModel.state.gunState.loadRemainTime -= timeStepSecs
    }
  },

  getNextVehiclePosition(
    vehicleModel: VehicleModel,
    timeStepSecs: number
  ) {
    const position = vehicleModel.state.position
    const angle = vehicleModel.state.angle
    const velocity = this.getVelocity(vehicleModel)
    const velocityX = velocity * Math.cos(angle)
    const velocityY = velocity * Math.sin(angle)
    const nextX = position.x + velocityX * timeStepSecs
    const nextY = position.y + velocityY * timeStepSecs
    return {
      x: nextX,
      y: nextY
    }
  },

  getVelocity(vehicleModel: VehicleModel) {
    const direction = vehicleModel.state.movingDirection
    const velocity = vehicleModel.specs.movingVelocity
    const angle = vehicleModel.state.angle
    const absAngle = Math.abs(angle)
    const criticalAngle = vehicleModel.specs.criticalAngle
    if (direction === MovingDirection.RIGHT) {
      if (angle > criticalAngle) {
        return 0
      } else if (angle > 0) {
        return (velocity * (criticalAngle - absAngle)) / criticalAngle
      } else if (angle > -criticalAngle) {
        return (velocity * (criticalAngle + absAngle)) / criticalAngle
      } else {
        return 2 * velocity
      }
    }
    if (direction === MovingDirection.LEFT) {
      if (angle < -criticalAngle) {
        return 0
      } else if (angle < 0) {
        return (-velocity * (criticalAngle - absAngle)) / criticalAngle
      } else if (angle < criticalAngle) {
        return (-velocity * (criticalAngle + absAngle)) / criticalAngle
      } else {
        return -2 * velocity
      }
    }
    return 0
  }
}
