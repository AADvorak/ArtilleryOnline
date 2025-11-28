import type { RoomSpecs } from '@/playground/data/specs'
import type { RoomModel } from '@/playground/data/model'
import type {BodyPosition, Position, Shift} from "@/playground/data/common";
import type {ParticleState} from "~/playground/data/state";
import {Segment} from "~/playground/data/geometry";

export const BattleUtils = {
  getRoomWidth(roomSpecs: RoomSpecs) {
    return roomSpecs.rightTop.x - roomSpecs.leftBottom.x
  },

  getRoomHeight(roomSpecs: RoomSpecs) {
    return roomSpecs.rightTop.y - roomSpecs.leftBottom.y
  },

  getGroundSegmentsBetween(xMin: number, xMax: number, roomModel: RoomModel): Segment[] {
    const positions: Position[] = this.getGroundIndexesBetween(xMin, xMax, roomModel)
        .map(index => this.getGroundPosition(index, roomModel))
    if (positions.length < 2) {
      return []
    }
    const segments: Segment[] = []
    for (let j = 0; j < positions.length - 1; j++) {
      segments.push(new Segment(positions[j]!, positions[j + 1]!))
    }
    return segments
  },

  getGroundIndexesBetween(xMin: number, xMax: number, roomModel: RoomModel): number[] {
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

  getGroundPosition(index: number, roomModel: RoomModel): Position {
    return {
      x: index * roomModel.specs.step,
      y: roomModel.state.groundLine[index]!
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

  shiftedBodyPosition(position: BodyPosition, distance: number, angle: number): BodyPosition {
    return {
      x: position.x + distance * Math.cos(angle),
      y: position.y + distance * Math.sin(angle),
      angle: position.angle
    }
  },

  shiftPosition(position: Position, shift: Shift) {
    position.x += shift.distance * Math.cos(shift.angle)
    position.y += shift.distance * Math.sin(shift.angle)
  },

  calculateAngleDiff(objectAngle: number, targetAngle: number): number {
    let diff = targetAngle - objectAngle

    if (Math.abs(diff) > Math.PI) {
      if (diff > 0) {
        return 2 * Math.PI - diff
      } else {
        return 2 * Math.PI + diff
      }
    } else {
      return diff
    }
  },

  generateParticle(position: Position, lifeTime?: number): ParticleState {
    const velocityMagnitude = 2 + 0.5 * Math.random()
    const velocityAngle = Math.PI / 4 + Math.PI * Math.random() / 2
    const remainTime = lifeTime || 0.3 * Math.random()
    const {x, y} = position
    return {
      position: {x, y},
      velocity: {
        x: velocityMagnitude * Math.cos(velocityAngle),
        y: velocityMagnitude * Math.sin(velocityAngle),
      },
      remainTime
    }
  }
}
