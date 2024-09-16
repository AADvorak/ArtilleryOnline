import type { RoomModel, VehicleModel } from '@/data/model'
import { BattleUtils } from '@/utils/battle-utils'
import type { Position } from '@/data/common'

export const VehicleOnGroundProcessor = {
  correctVehiclePositionAndAngleOnGround(vehicleModel: VehicleModel, roomModel: RoomModel) {
    const vehicleRadius = vehicleModel.specs.radius
    const wheelRadius = vehicleModel.specs.wheelRadius
    let angle = vehicleModel.state.angle
    const wheelDistance = vehicleModel.preCalc.wheelDistance
    const wheelAngle = vehicleModel.preCalc.wheelAngle
    const sign = angle > 0 ? -1 : 1
    const firstWheelX =
      vehicleModel.state.position.x - sign * wheelDistance * Math.cos(wheelAngle + sign * angle)
    const firstWheelPosition = this.getWheelPositionOnGround(firstWheelX, wheelRadius, roomModel)
    angle = this.getVehicleAngleOnGround(
      firstWheelPosition,
      sign,
      vehicleRadius,
      wheelRadius,
      roomModel
    )
    vehicleModel.state.position = {
      x: firstWheelPosition.x + sign * wheelDistance * Math.cos(sign * wheelAngle + angle),
      y: firstWheelPosition.y + sign * wheelDistance * Math.sin(sign * wheelAngle + angle)
    }
    vehicleModel.state.angle = angle
  },

  getWheelPositionOnGround(x: number, wheelRadius: number, roomModel: RoomModel) {
    const groundIndexes = BattleUtils.getGroundIndexesBetween(
      x - wheelRadius,
      x + wheelRadius,
      roomModel
    )
    let groundPosition = BattleUtils.getGroundPosition(groundIndexes[0], roomModel)
    let wheelPosition = {
      x: x,
      y: this.getWheelYOnGround(x, wheelRadius, groundPosition)
    }
    for (let i = 1; i < groundIndexes.length; i++) {
      groundPosition = BattleUtils.getGroundPosition(groundIndexes[i], roomModel)
      if (BattleUtils.distance(groundPosition, wheelPosition) < wheelRadius) {
        wheelPosition.y = this.getWheelYOnGround(x, wheelRadius, groundPosition)
      }
    }
    return wheelPosition
  },

  getWheelYOnGround(x: number, wheelRadius: number, groundPosition: Position) {
    return (
      groundPosition.y + Math.sqrt(Math.pow(wheelRadius, 2) - Math.pow(x - groundPosition.x, 2))
    )
  },

  getVehicleAngleOnGround(
    otherWheelPosition: Position,
    sign: number,
    vehicleRadius: number,
    wheelRadius: number,
    roomModel: RoomModel
  ) {
    const xMin =
      sign > 0 ? otherWheelPosition.x : otherWheelPosition.x - 2 * vehicleRadius - wheelRadius
    const xMax =
      sign > 0 ? otherWheelPosition.x + 2 * vehicleRadius + wheelRadius : otherWheelPosition.x
    const groundPositions = BattleUtils.getGroundIndexesBetween(xMin, xMax, roomModel)
      .map((index) => BattleUtils.getGroundPosition(index, roomModel))
      .filter(
        (groundPosition) =>
          BattleUtils.distance(groundPosition, otherWheelPosition) > 2 * vehicleRadius - wheelRadius
      )
    let vehicleAngle = this.calculateVehicleAngle(
      otherWheelPosition,
      sign,
      groundPositions[0],
      vehicleRadius,
      wheelRadius
    )
    for (let i = 1; i < groundPositions.length; i++) {
      const angle = this.calculateVehicleAngle(
        otherWheelPosition,
        sign,
        groundPositions[i],
        vehicleRadius,
        wheelRadius
      )
      if ((sign > 0 && angle > vehicleAngle) || (sign <= 0 && angle < vehicleAngle)) {
        vehicleAngle = angle
      }
    }
    return vehicleAngle
  },

  calculateVehicleAngle(
    otherWheelPosition: Position,
    sign: number,
    groundPosition: Position,
    vehicleRadius: number,
    wheelRadius: number
  ) {
    const xDiff = otherWheelPosition.x - groundPosition.x
    const yDiff = otherWheelPosition.y - groundPosition.y
    const sumCoefficient =
      (sign *
        (Math.pow(wheelRadius, 2) -
          Math.pow(xDiff, 2) -
          Math.pow(yDiff, 2) -
          Math.pow(2 * vehicleRadius, 2))) /
      (4 * vehicleRadius)
    const multiplierCoefficient = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2))
    const ratio = sumCoefficient / multiplierCoefficient
    return Math.asin(ratio > 1 ? 1 : ratio) - Math.asin(xDiff / multiplierCoefficient)
  }
}
