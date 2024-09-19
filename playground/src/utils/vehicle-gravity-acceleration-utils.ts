import { BattleUtils } from '@/utils/battle-utils'
import { VehicleUtils } from '@/utils/vehicle-utils'
import type { RoomModel, VehicleModel } from '@/data/model'
import type { Position } from '@/data/common'

export const VehicleGravityAccelerationUtils = {
  getVehicleGravityAcceleration(vehicleModel: VehicleModel, roomModel: RoomModel) {
    return (
      this.getLeftWheelAcceleration(vehicleModel, roomModel) +
      this.getRightWheelAcceleration(vehicleModel, roomModel)
    )
  },

  getRightWheelAcceleration(vehicleModel: VehicleModel, roomModel: RoomModel) {
    const wheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel)
    const groundPosition = this.getWheelNearestGroundPositions(
      wheelPosition,
      vehicleModel.specs.wheelRadius,
      roomModel
    )
    return this.getGravityAcceleration(
      wheelPosition,
      groundPosition,
      roomModel.specs.gravityAcceleration,
      vehicleModel.state.angle
    )
  },

  getLeftWheelAcceleration(vehicleModel: VehicleModel, roomModel: RoomModel) {
    const wheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel)
    const groundPosition = this.getWheelNearestGroundPositions(
      wheelPosition,
      vehicleModel.specs.wheelRadius,
      roomModel
    )
    return this.getGravityAcceleration(
      wheelPosition,
      groundPosition,
      roomModel.specs.gravityAcceleration,
      vehicleModel.state.angle
    )
  },

  getGravityAcceleration(
    wheelPosition: Position,
    groundPosition: Position,
    gravityAcceleration: number,
    vehicleAngle: number
  ) {
    const groundAngle = Math.atan(
      (groundPosition.x - wheelPosition.x) / Math.abs(wheelPosition.y - groundPosition.y)
    )
    return -0.5 * gravityAcceleration * Math.sin(groundAngle) * Math.cos(vehicleAngle - groundAngle)
  },

  getWheelNearestGroundPositions(
    wheelPosition: Position,
    wheelRadius: number,
    roomModel: RoomModel
  ) {
    const groundIndexes = BattleUtils.getGroundIndexesBetween(
      wheelPosition.x - wheelRadius,
      wheelPosition.x + wheelRadius,
      roomModel
    )
    let i = groundIndexes.length - 1
    let groundPosition = BattleUtils.getGroundPosition(groundIndexes[i], roomModel)
    let groundDistance = BattleUtils.distance(groundPosition, wheelPosition)

    if (groundDistance <= wheelRadius) {
      return groundPosition
    }

    for (i = groundIndexes.length - 2; i >= 0; i--) {
      const position = BattleUtils.getGroundPosition(groundIndexes[i], roomModel)
      const distance = BattleUtils.distance(position, wheelPosition)
      if (distance <= wheelRadius) {
        return position
      }
      if (distance <= groundDistance) {
        groundPosition = position
        groundDistance = distance
      }
    }
    return groundPosition
  }
}
