import type { BattleModel, VehicleModel } from '@/data/model'
import { MovingDirection } from '@/data/common'
import { VehicleOnGroundProcessor } from '@/processor/vehicle-on-ground-processor'
import { VehicleUtils } from '@/utils/vehicle-utils'
import { VehicleGravityAccelerationUtils } from '@/utils/vehicle-gravity-acceleration-utils'

export const VehicleProcessor = {
  processStep(vehicleModel: VehicleModel, battleModel: BattleModel, timeStepSecs: number) {
    this.recalculateVelocity(vehicleModel, battleModel, timeStepSecs)
    vehicleModel.state.position = this.getNextVehiclePosition(vehicleModel, timeStepSecs)
    VehicleOnGroundProcessor.correctVehiclePositionAndAngleOnGround(
        vehicleModel,
        battleModel.room
    )
    if (vehicleModel.state.gunRotatingDirection) {
      const sign = MovingDirection.RIGHT === vehicleModel.state.gunRotatingDirection ? -1 : 1
      vehicleModel.state.gunAngle += sign * vehicleModel.config.gun.rotationVelocity * timeStepSecs
    }
    if (vehicleModel.state.gunState.loadingShell) {
      vehicleModel.state.gunState.loadRemainTime -= timeStepSecs
    }
  },

  recalculateVelocity(vehicleModel: VehicleModel, battleModel: BattleModel, timeStepSecs: number) {
    const acceleration =
      VehicleUtils.getVehicleAcceleration(vehicleModel) +
      VehicleGravityAccelerationUtils.getVehicleGravityAcceleration(vehicleModel, battleModel.room)
    vehicleModel.state.velocity += timeStepSecs * acceleration
  },

  getNextVehiclePosition(vehicleModel: VehicleModel, timeStepSecs: number) {
    const position = vehicleModel.state.position
    const angle = vehicleModel.state.angle
    const velocity = vehicleModel.state.velocity
    const velocityX = velocity * Math.cos(angle)
    const velocityY = velocity * Math.sin(angle)
    const nextX = position.x + velocityX * timeStepSecs
    const nextY = position.y + velocityY * timeStepSecs
    return {
      x: nextX,
      y: nextY
    }
  }
}
