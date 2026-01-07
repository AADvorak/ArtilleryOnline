import type {Position} from "~/playground/data/common"
import {Contact, HitSurface} from "~/playground/data/common"
import {Circle, HalfCircle, Segment, Trapeze} from "~/playground/data/geometry"
import {GeometryUtils} from "~/playground/utils/geometry-utils"
import {VectorUtils} from "~/playground/utils/vector-utils"
import {type VehicleCalculations, type WheelCalculations} from "~/playground/data/calculations"
import {BodyUtils} from "~/playground/utils/body-utils"
import {BattleUtils} from "~/playground/utils/battle-utils"

export const TrajectoryContactUtils = {

  detectWithVehicle(projectileTrace: Segment, vehicle: VehicleCalculations): ContactAndHitSurface | undefined {
    const vehiclePart = BodyUtils.getBodyPart(vehicle.getModel().specs.turretShape, vehicle.getGeometryBodyPosition())
    const intersectionPointsMap = new Map<Position, ContactAndHitSurface>()
    
    if (vehiclePart instanceof HalfCircle) {
      GeometryUtils.getSegmentAndHalfCircleIntersectionPoints(projectileTrace, vehiclePart)
        .forEach(intersectionPoint => {
          const normal = VectorUtils.vectorFromTo(intersectionPoint, vehiclePart.center)
          VectorUtils.normalize(normal)
          const contact = Contact.withNormalUncheckedDepth(0.0, normal, intersectionPoint)
          intersectionPointsMap.set(intersectionPoint, new ContactAndHitSurface(contact, HitSurface.TOP))
        })
      this.findIntersectionPointAndPutToMap(projectileTrace, vehiclePart.chord(), HitSurface.BOTTOM, intersectionPointsMap)
    }
    
    if (vehiclePart instanceof Trapeze) {
      const sides = [vehiclePart.left(), vehiclePart.right()]
      sides.forEach(side => this.findIntersectionPointAndPutToMap(projectileTrace, side, HitSurface.SIDE, intersectionPointsMap))
      this.findIntersectionPointAndPutToMap(projectileTrace, vehiclePart.bottom(), HitSurface.BOTTOM, intersectionPointsMap)
      this.findIntersectionPointAndPutToMap(projectileTrace, vehiclePart.top(), HitSurface.TOP, intersectionPointsMap)
    }
    
    if (intersectionPointsMap.size > 0) {
      const closest = this.findClosestIntersectionPoint(projectileTrace.begin, Array.from(intersectionPointsMap.keys()))
      return intersectionPointsMap.get(closest)

    }
    return undefined
  },

  detectWithWheel(projectileTrace: Segment, wheel: WheelCalculations): Contact | undefined {
    const wheelPosition = wheel.position
    const wheelRadius = wheel.model.specs.wheelRadius
    const wheelShape = new Circle(wheelPosition!, wheelRadius)
    const intersectionPoints = GeometryUtils.getSegmentAndCircleIntersectionPoints(projectileTrace, wheelShape)
    if (intersectionPoints.size > 0) {
      const intersectionPoint = this.findClosestIntersectionPoint(projectileTrace.begin, Array.from(intersectionPoints))
      return  Contact.withNormalUncheckedDepth(
        0.0,
        VectorUtils.vectorFromTo(intersectionPoint, wheelPosition!),
        intersectionPoint
      )
    }
    return undefined
  },

  findClosestIntersectionPoint(position: Position, intersectionPoints: Position[]): Position {
    let closest = intersectionPoints[0]!
    let closestDistance = BattleUtils.distance(position, closest)
    for (let i = 1; i < intersectionPoints.length; i++) {
      const point = intersectionPoints[i]!
      const distance = BattleUtils.distance(position, point)
      if (distance < closestDistance) {
        closest = point
        closestDistance = distance
      }
    }
    return closest
  },

  findIntersectionPointAndPutToMap(
    projectileTrace: Segment,
    side: Segment,
    hitSurface: HitSurface,
    intersectionPointsMap: Map<Position, ContactAndHitSurface>
  ): void {
    const intersectionPoint = GeometryUtils.getSegmentsIntersectionPoint(projectileTrace, side)
    if (intersectionPoint) {
      const contact = Contact.withNormalUncheckedDepth(0.0, side.normal(), intersectionPoint)
      intersectionPointsMap.set(intersectionPoint, new ContactAndHitSurface(contact, hitSurface))
    }
  }
}

export class ContactAndHitSurface {
  constructor(
    readonly contact: Contact,
    readonly hitSurface: HitSurface
  ) {}
}
