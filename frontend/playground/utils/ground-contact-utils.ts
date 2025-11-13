import type {Contact, Position, Vector} from "~/playground/data/common";
import type {RoomModel} from "~/playground/data/model";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {type Circle, HalfCircle, Segment, Trapeze} from "~/playground/data/geometry";
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

      const bottomContact: Contact | null = this.getSegmentGroundContact(bottom,
          position, groundIndex, roomModel, 'HalfCircle bottom with ground')

      let topContact: Contact | null = null
      const distance = BattleUtils.distance(halfCircle.center, position)
      const radiusVector = VectorUtils.vectorFromTo(halfCircle.center, position)

      if (distance < halfCircle.radius && VectorUtils.dotProduct(halfCircleNormal, radiusVector) > 0) {
        const depth = halfCircle.radius - distance
        const contactPosition = BattleUtils.shiftedPosition(halfCircle.center, halfCircle.radius,
            VectorUtils.angleFromTo(halfCircle.center, position))
        topContact = this.createContact.withAngle(
            depth,
            this.getGroundAngle(position, groundIndex, roomModel),
            contactPosition,
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

  getTrapezeGroundContacts(
      trapeze: Trapeze,
      roomModel: RoomModel
  ): Set<Contact> {
    const maxDistance = trapeze.maxDistanceFromCenter()
    const groundIndexes = BattleUtils.getGroundIndexesBetween(
        trapeze.position.x - maxDistance,
        trapeze.position.x + maxDistance,
        roomModel
    )
    const contacts = new Set<Contact>()

    if (groundIndexes.length === 0) {
      return contacts
    }

    const bottom = trapeze.bottom()
    const top = trapeze.top()
    const right = trapeze.right()
    const left = trapeze.left()

    for (const index of groundIndexes) {
      const position = BattleUtils.getGroundPosition(index, roomModel)

      const bottomContact = this.getSegmentGroundContact(bottom, position, index, roomModel, 'Trapeze bottom with ground')
      const topContact = this.getSegmentGroundContact(top, position, index, roomModel, 'Trapeze top with ground')
      const rightContact = this.getSegmentGroundContact(right, position, index, roomModel, 'Trapeze right with ground')
      const leftContact = this.getSegmentGroundContact(left, position, index, roomModel, 'Trapeze left with ground')

      let resultContact: Contact | null = null

      if (topContact !== null) {
        resultContact = topContact
      }
      if (bottomContact !== null && (resultContact === null || bottomContact.depth < resultContact.depth)) {
        resultContact = bottomContact
      }
      if (rightContact !== null && (resultContact === null || rightContact.depth < resultContact.depth)) {
        resultContact = rightContact
      }
      if (leftContact !== null && (resultContact === null || leftContact.depth < resultContact.depth)) {
        resultContact = leftContact
      }

      if (resultContact !== null && resultContact.depth >= Constants.INTERPENETRATION_THRESHOLD) {
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
    const groundSegments = BattleUtils.getGroundSegmentsBetween(
        circle.center.x - circle.radius * 1.1,
        circle.center.x + circle.radius * 1.1,
        roomModel
    )

    if (groundSegments.length === 0) return null

    let nearestPosition: Position | null = null
    let minimalDistance: number | null = null
    let nearestSegment: Segment | null = null

    for (const segment of groundSegments) {
      const position = GeometryUtils.getPointToSegmentProjection(circle.center, segment)
      if (position) {
        const distance = BattleUtils.distance(position, circle.center)

        if (distance < circle.radius &&
            (minimalDistance === null || distance < minimalDistance)) {
          nearestPosition = position
          minimalDistance = distance
          nearestSegment = segment
        }
      }
    }

    if (nearestPosition && minimalDistance !== null && nearestSegment !== null) {
      const contactPosition = BattleUtils.shiftedPosition(circle.center, circle.radius,
          VectorUtils.angleFromTo(circle.center, nearestPosition))
      let depth = BattleUtils.distance(nearestPosition, contactPosition)
      if (withMaxDepth) depth -= roomModel.specs.groundMaxDepth
      const groundAngle = VectorUtils.angleFromTo(nearestSegment.begin, nearestSegment.end)
      return this.createContact.withAngle(
          depth,
          groundAngle,
          contactPosition
      )
    }

    const fallbackPosition = BattleUtils.getNearestGroundPosition(circle.center.x, roomModel)
    let depth = this.getGroundDepth.fullUnderGround(circle, fallbackPosition.y)
    if (withMaxDepth) depth -= roomModel.specs.groundMaxDepth
    const contactPosition = BattleUtils.shiftedPosition(circle.center, circle.radius, -Math.PI / 2)
    return this.createContact.withAngle(
        depth,
        0,
        contactPosition
    )
  },

  getSegmentGroundContact(
      segment: Segment,
      groundPosition: Position,
      groundIndex: number,
      roomModel: RoomModel,
      description: string
  ): Contact | null {
    const segmentPoint = segment.findPointWithX(groundPosition.x)
    if (segmentPoint !== null && segmentPoint.y < groundPosition.y) {
      const projection = GeometryUtils.getPointToSegmentProjection(groundPosition, segment)
      if (projection !== null) {
        const depth = BattleUtils.distance(groundPosition, segmentPoint)
        const normal1 = VectorUtils.vectorFromTo(groundPosition, projection)
        VectorUtils.normalize(normal1)
        const normal2 = VectorUtils.getNormal(this.getGroundAngle(groundPosition, groundIndex, roomModel))
        VectorUtils.normalize(normal2)
        const normal = VectorUtils.sumOf(normal1, normal2)
        VectorUtils.normalize(normal)
        return this.createContact.withNormal(depth, normal, projection, description)
      }
    }
    return null
  },

  getGroundDepth: {
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
