import type { Position } from '@/data/common'
import type { RoomModel } from '@/data/model'
import type { NearestGroundPoint, WheelCalculations } from '@/data/calculations'
import { BattleUtils } from '@/utils/battle-utils'

export const GroundPositionCalculator = {
  calculate(wheelCalculations: WheelCalculations, wheelRadius: number, roomModel: RoomModel): void {
    wheelCalculations.nearestGroundPointByX = BattleUtils.getNearestGroundPosition(
      wheelCalculations.position!.x,
      roomModel
    )

    const nearestGroundPoint = this.getNearestGroundPoint(
      wheelCalculations.position!,
      wheelRadius,
      roomModel,
      wheelCalculations.sign
    )

    if (nearestGroundPoint == null) {
      return
    }

    wheelCalculations.nearestGroundPoint = nearestGroundPoint
    wheelCalculations.groundAngle = this.getGroundAngle(
      wheelCalculations.position!,
      nearestGroundPoint,
      roomModel
    )

    if (nearestGroundPoint.position.y <= wheelCalculations.position!.y) {
      wheelCalculations.groundDepth = wheelRadius - nearestGroundPoint.distance
    } else {
      wheelCalculations.groundDepth = wheelRadius + nearestGroundPoint.distance
    }
  },

  getGroundAngle(
    position: Position,
    nearestGroundPoint: NearestGroundPoint,
    roomModel: RoomModel
  ): number {
    if (nearestGroundPoint.index > 0 && nearestGroundPoint.position.x <= position.x) {
      const otherGroundPosition = BattleUtils.getGroundPosition(
        nearestGroundPoint.index - 1,
        roomModel
      )
      return Math.atan(
        (nearestGroundPoint.position.y - otherGroundPosition.y) /
          (nearestGroundPoint.position.x - otherGroundPosition.x)
      )
    } else {
      const otherGroundPosition = BattleUtils.getGroundPosition(
        nearestGroundPoint.index + 1,
        roomModel
      )
      return Math.atan(
        (otherGroundPosition.y - nearestGroundPoint.position.y) /
          (otherGroundPosition.x - nearestGroundPoint.position.x)
      )
    }
  },

  getNearestGroundPoint(
    objectPosition: Position,
    objectRadius: number,
    roomModel: RoomModel,
    sign: number
  ): NearestGroundPoint | undefined {
    const groundIndexes = BattleUtils.getGroundIndexesBetween(
      objectPosition.x - objectRadius,
      objectPosition.x + objectRadius,
      roomModel
    )

    if (groundIndexes.length === 0) {
      return undefined
    }

    let nearestPosition: Position | undefined
    let minimalDistance: number | undefined
    let index: number | undefined
    let i = sign > 0 ? 0 : groundIndexes.length - 1

    while (i >= 0 && i < groundIndexes.length) {
      const position = BattleUtils.getGroundPosition(groundIndexes[i], roomModel)
      const distance = BattleUtils.distance(position, objectPosition)

      if (distance <= objectRadius) {
        if (!minimalDistance || distance < minimalDistance) {
          nearestPosition = position
          minimalDistance = distance
          index = groundIndexes[i]
        }
      }
      i += sign
    }

    if (!nearestPosition) {
      return undefined
    }

    return { position: nearestPosition, distance: minimalDistance!, index: index! }
  }
}
