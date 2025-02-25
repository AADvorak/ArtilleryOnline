import type { RoomSpecs } from '@/playground/data/specs'
import type { RoomModel } from '@/playground/data/model'
import type {Position} from "@/playground/data/common";

export const BattleUtils = {
  getRoomWidth(roomSpecs: RoomSpecs) {
    return roomSpecs.rightTop.x - roomSpecs.leftBottom.x
  },

  getRoomHeight(roomSpecs: RoomSpecs) {
    return roomSpecs.rightTop.y - roomSpecs.leftBottom.y
  },

  getGroundIndexesBetween(xMin: number, xMax: number, roomModel: RoomModel) {
    const roomWidth = this.getRoomWidth(roomModel.specs)
    const groundPointsNumber = roomModel.state.groundLine.length
    let minGroundIndex = Math.ceil((groundPointsNumber * xMin) / roomWidth)
    let maxGroundIndex = Math.floor((groundPointsNumber * xMax) / roomWidth)

    if (minGroundIndex < 0) {
      minGroundIndex = 0
    }
    if (maxGroundIndex >= groundPointsNumber) {
      maxGroundIndex = groundPointsNumber - 1
    }

    const groundIndexes = []
    for (let i = minGroundIndex; i < maxGroundIndex; i++) {
      groundIndexes.push(i)
    }

    return groundIndexes
  },

  getNearestGroundPosition(x: number, roomModel: RoomModel) {
    const roomWidth = this.getRoomWidth(roomModel.specs)
    const groundPointsNumber = roomModel.state.groundLine.length
    const objectPositionIndex = (groundPointsNumber * x) / roomWidth
    let nearestGroundIndex = Math.floor(objectPositionIndex)

    if (nearestGroundIndex < 0) {
      nearestGroundIndex = 0
    }
    if (nearestGroundIndex >= groundPointsNumber) {
      nearestGroundIndex = groundPointsNumber - 1
    }

    return this.getGroundPosition(nearestGroundIndex, roomModel)
  },

  getGroundPosition(index: number, roomModel: RoomModel) {
    return {
      x: index * roomModel.specs.step,
      y: roomModel.state.groundLine[index]
    }
  },

  distance(p1: Position, p2: Position) {
    return Math.sqrt(Math.pow(p1.x - p2.x, 2.0) + Math.pow(p1.y - p2.y, 2.0));
  },

  shiftedPosition(position: Position, distance: number, angle: number): Position {
    return {
      x: position.x + distance * Math.cos(angle),
      y: position.y + distance * Math.sin(angle)
    }
  },

  calculateAngleDiff(missileAngle: number, targetAngle: number): number {
    let diff = targetAngle - missileAngle

    if (Math.abs(diff) > Math.PI) {
      if (diff > 0) {
        return 2 * Math.PI - diff
      } else {
        return 2 * Math.PI + diff
      }
    } else {
      return diff
    }
  }
}
