import type {Contact, Position, Vector} from "~/playground/data/common";
import type {RoomModel} from "~/playground/data/model";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {type Circle, HalfCircle} from "~/playground/data/geometry";
import {GeometryUtils} from "~/playground/utils/geometry-utils";
import {Constants} from "~/playground/data/constants";

export const GroundContactUtils = {

  getHalfCircleGroundContacts(
      halfCircle: HalfCircle,
      roomModel: RoomModel
  ): Set<Contact> {
    const groundIndexes = BattleUtils.getGroundIndexesBetween(
        halfCircle.center.x - halfCircle.radius,
        halfCircle.center.x + halfCircle.radius,
        roomModel
    )
    const contacts = new Set<Contact>()

    if (groundIndexes.length === 0) return contacts

    const bottom = halfCircle.chord()
    const halfCircleNormal = VectorUtils.vectorFromTo(
        halfCircle.center,
        BattleUtils.shiftedPosition(halfCircle.center, 1, halfCircle.angle + Math.PI / 2)
    )

    for (const groundIndex of groundIndexes) {
      const position = BattleUtils.getGroundPosition(groundIndex, roomModel)

      let bottomContact: Contact | null = null
      const bottomPoint = bottom.findPointWithX(position.x)
      if (bottomPoint && bottomPoint.y < position.y) {
        const projection = GeometryUtils.getPointToSegmentProjection(position, bottom)
        if (projection) {
          let depth = BattleUtils.distance(position, projection)
          const normal = VectorUtils.vectorFromTo(position, projection)
          VectorUtils.normalize(normal)
          bottomContact = this.createContact.withNormal(
              depth,
              normal,
              position,
              "HalfCircle bottom with ground"
          )
        }
      }

      let topContact: Contact | null = null
      const distance = BattleUtils.distance(halfCircle.center, position)
      const radiusVector = VectorUtils.vectorFromTo(halfCircle.center, position)

      if (distance < halfCircle.radius && VectorUtils.dotProduct(halfCircleNormal, radiusVector) > 0) {
        let depth = halfCircle.radius - distance
        topContact = this.createContact.withAngle(
            depth,
            this.getGroundAngle(position, groundIndex, roomModel),
            position,
            "HalfCircle top with ground"
        )
      }

      let resultContact: Contact | null = null
      if (topContact && bottomContact) {
        resultContact = topContact.depth < bottomContact.depth ? topContact : bottomContact
      } else if (topContact) {
        resultContact = topContact
      } else if (bottomContact) {
        resultContact = bottomContact
      }

      if (resultContact) {
        contacts.add(resultContact)
      }
    }

    return contacts
  },

  getCircleGroundContact(
      circle: Circle,
      roomModel: RoomModel,
      withMaxDepth: boolean
  ): Contact | null {
    const groundIndexes = BattleUtils.getGroundIndexesBetween(
        circle.center.x - circle.radius,
        circle.center.x + circle.radius,
        roomModel
    )

    if (groundIndexes.length === 0) return null

    let nearestPosition: Position | null = null
    let minimalDistance: number | null = null
    let index: number | null = null

    for (const groundIndex of groundIndexes) {
      const position = BattleUtils.getGroundPosition(groundIndex, roomModel)
      const distance = BattleUtils.distance(position, circle.center)

      if (distance < circle.radius &&
          (minimalDistance === null || distance < minimalDistance)) {
        nearestPosition = position
        minimalDistance = distance
        index = groundIndex
      }
    }

    if (nearestPosition && minimalDistance !== null && index !== null) {
      let depth = this.getGroundDepth.halfUnderGround(circle, nearestPosition.y, minimalDistance)
      if (withMaxDepth) depth -= roomModel.specs.groundMaxDepth

      return this.createContact.withAngle(
          depth,
          this.getGroundAngle(nearestPosition, index, roomModel),
          nearestPosition
      )
    }

    const fallbackPosition = BattleUtils.getNearestGroundPosition(circle.center.x, roomModel)
    let depth = this.getGroundDepth.fullUnderGround(circle, fallbackPosition.y)
    if (withMaxDepth) depth -= roomModel.specs.groundMaxDepth

    return this.createContact.withAngle(
        depth,
        0,
        fallbackPosition
    )
  },

  getGroundDepth: {
    halfUnderGround(circle: Circle, y: number, distance: number): number {
      return y <= circle.center.y
          ? circle.radius - distance
          : circle.radius + distance
    },

    fullUnderGround(circle: Circle, y: number): number {
      const depth = y - circle.center.y + circle.radius
      return depth < 0 ? 0 : depth
    }
  },

  getGroundAngle(
      groundPosition: Position,
      groundIndex: number,
      roomModel: RoomModel
  ): number {
    const angles: number[] = []
    const prevPosition = BattleUtils.getGroundPosition(groundIndex - 1, roomModel)
    if (prevPosition) {
      angles.push(VectorUtils.angleFromTo(prevPosition, groundPosition))
    }
    const nextPosition = BattleUtils.getGroundPosition(groundIndex + 1, roomModel)
    if (nextPosition) {
      angles.push(VectorUtils.angleFromTo(groundPosition, nextPosition))
    }
    return angles.length > 0
        ? angles.reduce((sum, angle) => sum + angle, 0) / angles.length
        : 0
  },

  createContact: {
    withAngle(depth: number, angle: number, position: Position, description?: string): Contact | null {
      return this.checkDepth({
        depth,
        angle,
        normal: VectorUtils.getNormal(angle),
        position,
        description
      })
    },

    withNormal(depth: number, normal: Vector, position: Position, description?: string): Contact | null {
      return this.checkDepth({
        depth,
        angle: VectorUtils.getAngle(normal) + Math.PI / 2,
        normal,
        position,
        description
      })
    },

    checkDepth(contact: Contact): Contact | null {
      if (contact.depth < Constants.INTERPENETRATION_THRESHOLD) {
        return null
      }
      return contact
    }
  }
}
