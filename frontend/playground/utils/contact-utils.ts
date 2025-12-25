import {Contact, type Position, type Vector} from "~/playground/data/common";
import {type BodyPart, Circle, HalfCircle, Polygon, Trapeze, Segment} from "~/playground/data/geometry";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {GeometryUtils} from "~/playground/utils/geometry-utils";

export class ContactUtils {
  static getBodyPartsContact(bodyPart: BodyPart, otherBodyPart: BodyPart): Contact | null {
    if (bodyPart instanceof Circle) {
      const circle = bodyPart as Circle

      if (otherBodyPart instanceof Circle) {
        const otherCircle = otherBodyPart as Circle
        return ContactUtils.getCirclesContact(circle, otherCircle)
      }

      if (otherBodyPart instanceof HalfCircle) {
        const otherHalfCircle = otherBodyPart as HalfCircle
        return ContactUtils.getCircleHalfCircleContact(circle, otherHalfCircle)
      }

      if (otherBodyPart instanceof Trapeze) {
        const otherTrapeze = otherBodyPart as Trapeze
        const contact = ContactUtils.getTrapezeCircleContact(otherTrapeze, circle)
        return contact ? contact.inverted() : null
      }
    }

    if (bodyPart instanceof HalfCircle) {
      const halfCircle = bodyPart as HalfCircle

      if (otherBodyPart instanceof Circle) {
        const otherCircle = otherBodyPart as Circle
        const contact = ContactUtils.getCircleHalfCircleContact(otherCircle, halfCircle)
        return contact ? contact.inverted() : null
      }

      if (otherBodyPart instanceof HalfCircle) {
        const otherHalfCircle = otherBodyPart as HalfCircle
        return ContactUtils.getHalfCirclesContact(halfCircle, otherHalfCircle)
      }

      if (otherBodyPart instanceof Trapeze) {
        const otherTrapeze = otherBodyPart as Trapeze
        const contact = ContactUtils.getTrapezeHalfCircleContact(otherTrapeze, halfCircle)
        return contact ? contact.inverted() : null
      }
    }

    if (bodyPart instanceof Trapeze) {
      const trapeze = bodyPart as Trapeze

      if (otherBodyPart instanceof Circle) {
        const otherCircle = otherBodyPart as Circle
        return ContactUtils.getTrapezeCircleContact(trapeze, otherCircle)
      }

      if (otherBodyPart instanceof HalfCircle) {
        const otherHalfCircle = otherBodyPart as HalfCircle
        return ContactUtils.getTrapezeHalfCircleContact(trapeze, otherHalfCircle)
      }

      if (otherBodyPart instanceof Trapeze) {
        const otherTrapeze = otherBodyPart as Trapeze
        return ContactUtils.getTrapezesContact(trapeze, otherTrapeze)
      }
    }

    return null
  }

  static getCirclesContact(circle: Circle, otherCircle: Circle): Contact | null {
    const distance = BattleUtils.distance(circle.center, otherCircle.center)
    const minDistance = circle.radius + otherCircle.radius
    const depth = distance < minDistance ? minDistance - distance : 0
    const normal = VectorUtils.vectorFromTo(circle.center, otherCircle.center)
    VectorUtils.normalize(normal)
    const position = VectorUtils.shifted(circle.center,
        VectorUtils.multiply(normal, circle.radius - depth / 2))
    return Contact.withNormal(depth, normal, position)
  }

  static getCircleHalfCircleContact(circle: Circle, otherHalfCircle: HalfCircle): Contact | null {
    if (BattleUtils.distance(circle.center, otherHalfCircle.center) > circle.radius + otherHalfCircle.radius) {
      return null
    }

    const otherCircle = otherHalfCircle.circle()
    const intersectionPoints = GeometryUtils.getCirclesIntersectionPoints(circle, otherCircle)
    
    for (const intersectionPoint of intersectionPoints) {
      const pointAngle = VectorUtils.angleFromTo(otherHalfCircle.center, intersectionPoint)
      if (GeometryUtils.isPointLyingOnArc(pointAngle, otherHalfCircle.angle, otherHalfCircle.angle + Math.PI)) {
        return ContactUtils.getCirclesContact(circle, otherCircle)
      }
    }

    const otherBottom = otherHalfCircle.chord()
    const contact = ContactUtils.getSegmentAndCircleContact(otherBottom, circle)
    return contact ? contact.inverted() : null
  }

  static getHalfCirclesContact(halfCircle: HalfCircle, otherHalfCircle: HalfCircle): Contact | null {
    if (BattleUtils.distance(halfCircle.center, otherHalfCircle.center) > halfCircle.radius + otherHalfCircle.radius) {
      return null
    }

    const circle = halfCircle.circle()
    const otherCircle = otherHalfCircle.circle()
    const intersectionPoints = GeometryUtils.getCirclesIntersectionPoints(circle, otherCircle)
    
    for (const intersectionPoint of intersectionPoints) {
      const pointAngle1 = VectorUtils.angleFromTo(halfCircle.center, intersectionPoint)
      const pointAngle2 = VectorUtils.angleFromTo(otherHalfCircle.center, intersectionPoint)
      
      if (GeometryUtils.isPointLyingOnArc(pointAngle1, halfCircle.angle, halfCircle.angle + Math.PI) &&
          GeometryUtils.isPointLyingOnArc(pointAngle2, otherHalfCircle.angle, otherHalfCircle.angle + Math.PI)) {
        return ContactUtils.getCirclesContact(circle, otherCircle)
      }
    }

    const bottom = halfCircle.chord()
    if (ContactUtils.isHalfCircleBottomAndOtherHalfCircleTopContact(bottom, otherHalfCircle)) {
      return ContactUtils.getSegmentAndCircleContact(bottom, otherCircle)
    }

    const otherBottom = otherHalfCircle.chord()
    if (ContactUtils.isHalfCircleBottomAndOtherHalfCircleTopContact(otherBottom, halfCircle)) {
      const contact = ContactUtils.getSegmentAndCircleContact(otherBottom, circle)
      return contact ? contact.inverted() : null
    }

    return null
  }

  static getTrapezeCircleContact(trapeze: Trapeze, circle: Circle): Contact | null {
    if (BattleUtils.distance(trapeze.position, circle.center) > trapeze.maxDistanceFromCenter() + circle.radius) {
      return null
    }

    const edges = [trapeze.bottomLeft(), trapeze.bottomRight(), trapeze.topLeft(), trapeze.topRight()]
    for (const edge of edges) {
      const contact = ContactUtils.getPointAndCircleContact(edge, circle)
      if (contact) {
        return contact
      }
    }

    const polygon = new Polygon(trapeze)
    for (const side of polygon.sides()) {
      const contact = ContactUtils.getSegmentAndCircleContact(side, circle)
      if (contact) {
        return contact
      }
    }

    let minInnerDistance: number | null = null
    let closestSide: Segment | null = null
    
    for (const side of polygon.sides()) {
      const distance = ContactUtils.innerDistanceFromPointToSegment(circle.center, side)
      if (distance === 0) {
        return null
      }
      if (minInnerDistance === null || distance < minInnerDistance) {
        minInnerDistance = distance
        closestSide = side
      }
    }

    return Contact.withNormal(minInnerDistance! + circle.radius, VectorUtils.inverted(closestSide!.normal()), circle.center)
  }

  static getTrapezeHalfCircleContact(trapeze: Trapeze, halfCircle: HalfCircle): Contact | null {
    if (BattleUtils.distance(trapeze.position, halfCircle.center) > trapeze.maxDistanceFromCenter() + halfCircle.radius) {
      return null
    }

    const polygon = new Polygon(trapeze)
    const halfCircleTopIntersections = new Set<Segment>()
    const halfCircleBottomIntersections = new Set<Segment>()
    const halfCircleBottom = halfCircle.chord()
    const halfCircleBottomLeft = halfCircle.bottomLeft()
    const halfCircleBottomRight = halfCircle.bottomRight()
    const circle = halfCircle.circle()

    for (const side of polygon.sides()) {
      if (GeometryUtils.getSegmentsIntersectionPoint(side, halfCircleBottom) !== null) {
        halfCircleBottomIntersections.add(side)
      }
      
      GeometryUtils.getSegmentAndHalfCircleIntersectionPoints(side, halfCircle)
        .forEach(point => halfCircleTopIntersections.add(side))
    }

    if (halfCircleTopIntersections.size === 2) {
      const iterator = halfCircleTopIntersections.values()
      const firstSide = iterator.next().value!
      const secondSide = iterator.next().value!
      
      if (polygon.next(firstSide) === secondSide) {
        return ContactUtils.getPointAndCircleContact(firstSide.end, circle)
      } else if (polygon.next(secondSide) === firstSide) {
        return ContactUtils.getPointAndCircleContact(secondSide.end, circle)
      } else {
        const contacts = new Set<Contact>()
        this.addContactToSetIfNotNull(contacts, ContactUtils.getPointAndCircleContact(firstSide.end, circle))
        this.addContactToSetIfNotNull(contacts, ContactUtils.getPointAndCircleContact(secondSide.end, circle))
        this.addContactToSetIfNotNull(contacts, ContactUtils.getPointAndCircleContact(firstSide.begin, circle))
        this.addContactToSetIfNotNull(contacts, ContactUtils.getPointAndCircleContact(secondSide.begin, circle))
        this.addContactToSetIfNotNull(contacts, ContactUtils.getPointAndCircleContact(
            polygon.next(firstSide)!.center(), circle))
        this.addContactToSetIfNotNull(contacts, ContactUtils.getPointAndCircleContact(
            polygon.next(secondSide)!.center(), circle))
        return ContactUtils.getDeepestContact(contacts)
      }
    }

    if (halfCircleBottomIntersections.size === 2) {
      const iterator = halfCircleBottomIntersections.values()
      const firstSide = iterator.next().value!
      const secondSide = iterator.next().value!
      
      if (polygon.next(firstSide) === secondSide) {
        return ContactUtils.getPointAndSegmentContact(firstSide.end, halfCircleBottom, false)
      } else if (polygon.next(secondSide) === firstSide) {
        return ContactUtils.getPointAndSegmentContact(secondSide.end, halfCircleBottom, false)
      } else {
        const contacts = new Set<Contact>()
        this.addContactToSetIfNotNull(contacts, ContactUtils.getPointAndSegmentContact(
            firstSide.end, halfCircleBottom, true))
        this.addContactToSetIfNotNull(contacts, ContactUtils.getPointAndSegmentContact(
            secondSide.end, halfCircleBottom, true))
        this.addContactToSetIfNotNull(contacts, ContactUtils.getPointAndSegmentContact(
            firstSide.begin, halfCircleBottom, true))
        this.addContactToSetIfNotNull(contacts, ContactUtils.getPointAndSegmentContact(
            secondSide.begin, halfCircleBottom, true))
        return ContactUtils.getDeepestContact(contacts)
      }
    }

    if (halfCircleTopIntersections.size === 1 && halfCircleBottomIntersections.size === 1) {
      const firstSide = Array.from(halfCircleTopIntersections)[0]!
      const secondSide = Array.from(halfCircleBottomIntersections)[0]!
      
      if (firstSide === secondSide) {
        let contact = ContactUtils.getPointAndSegmentContact(halfCircleBottomLeft, firstSide, true)
        if (contact) {
          return contact.inverted()
        }
        contact = ContactUtils.getPointAndSegmentContact(halfCircleBottomRight, firstSide, true)
        if (contact) {
          return contact.inverted()
        }
      } else {
        let otherEdge: Position | null = null
        
        if (ContactUtils.isPointOnInnerSideOfSegment(halfCircleBottomLeft, firstSide) &&
            ContactUtils.isPointOnInnerSideOfSegment(halfCircleBottomLeft, secondSide)) {
          otherEdge = halfCircleBottomLeft
        } else if (ContactUtils.isPointOnInnerSideOfSegment(halfCircleBottomRight, firstSide) &&
                   ContactUtils.isPointOnInnerSideOfSegment(halfCircleBottomRight, secondSide)) {
          otherEdge = halfCircleBottomRight
        }

        if (otherEdge !== null) {
          let edge: Position | null = null
          
          if (polygon.next(firstSide) === secondSide) {
            edge = firstSide.end
          } else if (polygon.next(secondSide) === firstSide) {
            edge = secondSide.end
          }

          if (edge !== null) {
            const normal = VectorUtils.vectorFromTo(otherEdge, edge)
            VectorUtils.normalize(normal)
            return Contact.withNormal(
              BattleUtils.distance(edge, otherEdge),
              normal,
              new Segment(edge, otherEdge).center()
            )
          }
        }
      }
    }

    if (halfCircleTopIntersections.size === 1) {
      const side = Array.from(halfCircleTopIntersections)[0]!
      return ContactUtils.getSegmentAndCircleContact(side, circle)
    }

    return null
  }

  static getTrapezesContact(trapeze: Trapeze, otherTrapeze: Trapeze): Contact | null {
    const maxDistance = trapeze.maxDistanceFromCenter()
    const otherMaxDistance = otherTrapeze.maxDistanceFromCenter()
    
    if (BattleUtils.distance(trapeze.position, otherTrapeze.position) > maxDistance + otherMaxDistance) {
      return null
    }

    const polygon = new Polygon(trapeze)
    const otherPolygon = new Polygon(otherTrapeze)
    const otherSidesIntersections = new Map<Segment, Set<Segment>>()
    
    otherPolygon.sides().forEach(otherSide => otherSidesIntersections.set(otherSide, new Set<Segment>()))
    
    otherSidesIntersections.forEach((intersections, otherSide) => {
      polygon.sides().forEach(side => {
        if (GeometryUtils.getSegmentsIntersectionPoint(otherSide, side) !== null) {
          intersections.add(side)
        }
      })
    })

    for (const [otherSide, intersections] of otherSidesIntersections) {
      if (intersections.size === 2) {
        const iterator = intersections.values()
        const firstSide = iterator.next().value!
        const secondSide = iterator.next().value!
        
        if (polygon.next(firstSide) === secondSide) {
          return ContactUtils.getPointAndSegmentContact(firstSide.end, otherSide, false)
        } else if (polygon.next(secondSide) === firstSide) {
          return ContactUtils.getPointAndSegmentContact(secondSide.end, otherSide, false)
        } else {
          const contacts = new Set<Contact>()
          this.addContactToSetIfNotNull(contacts, ContactUtils.getPointAndSegmentContact(firstSide.end, otherSide, true))
          this.addContactToSetIfNotNull(contacts, ContactUtils.getPointAndSegmentContact(secondSide.end, otherSide, true))
          this.addContactToSetIfNotNull(contacts, ContactUtils.getPointAndSegmentContact(firstSide.begin, otherSide, true))
          this.addContactToSetIfNotNull(contacts, ContactUtils.getPointAndSegmentContact(secondSide.begin, otherSide, true))
          return ContactUtils.getDeepestContact(contacts)
        }
      } else if (intersections.size === 1) {
        const firstSide = Array.from(intersections)[0]!
        const secondOtherSide = otherPolygon.next(otherSide)!
        
        if (otherSidesIntersections.get(secondOtherSide)?.size === 1) {
          const secondSide = Array.from(otherSidesIntersections.get(secondOtherSide)!)[0]!
          
          if (firstSide === secondSide) {
            return null
          }

          const edge = polygon.next(firstSide) === secondSide ? firstSide.end : secondSide.end
          const otherEdge = otherSide.end
          const edgeOtherSideProjection = GeometryUtils.getPointToLineProjection(edge, otherSide)
          const edgeOtherSideDistance = BattleUtils.distance(edge, edgeOtherSideProjection)
          const edgeSecondOtherSideProjection = GeometryUtils.getPointToLineProjection(edge, secondOtherSide)
          const edgeSecondOtherSideDistance = BattleUtils.distance(edge, edgeSecondOtherSideProjection)
          const edgeOtherEdgeDistance = BattleUtils.distance(edge, otherEdge)

          if (edgeOtherEdgeDistance < 2 * edgeOtherSideDistance && edgeOtherEdgeDistance < 2 * edgeSecondOtherSideDistance) {
            const normal = VectorUtils.vectorFromTo(otherEdge, edge)
            VectorUtils.normalize(normal)
            return Contact.withNormal(
              edgeOtherEdgeDistance,
              normal,
              new Segment(edge, otherEdge).center()
            )
          } else if (edgeOtherSideDistance < edgeSecondOtherSideDistance) {
            const normal = VectorUtils.vectorFromTo(edgeOtherSideProjection, edge)
            VectorUtils.normalize(normal)
            return Contact.withNormal(
              edgeOtherSideDistance,
              normal,
              new Segment(edge, edgeOtherSideProjection).center()
            )
          } else {
            const normal = VectorUtils.vectorFromTo(edgeSecondOtherSideProjection, edge)
            VectorUtils.normalize(normal)
            return Contact.withNormal(
              edgeSecondOtherSideDistance,
              normal,
              new Segment(edge, edgeSecondOtherSideProjection).center()
            )
          }
        }
      }
    }

    return null
  }

  static getPolygonsContact(p1: Polygon, p2: Polygon): Contact | null {
    let bestContact: Contact | null = null
    bestContact = ContactUtils.checkPolygonAxes(p1, p2, bestContact, false)
    if (bestContact === null) {
      return null
    }
    bestContact = ContactUtils.checkPolygonAxes(p2, p1, bestContact, true)
    return bestContact
  }

  private static checkPolygonAxes(sourcePolygon: Polygon, targetPolygon: Polygon, bestContact: Contact | null, invert: boolean): Contact | null {
    for (const side of sourcePolygon.sides()) {
      const contact = ContactUtils.checkSeparatingAxis(sourcePolygon, targetPolygon, side.normal())
      if (contact === null) {
        return null
      }
      if (bestContact === null || contact.depth < bestContact.depth) {
        bestContact = invert ? contact.inverted() : contact
      }
    }
    return bestContact
  }

  private static checkSeparatingAxis(p1: Polygon, p2: Polygon, axis: Vector): Contact | null {
    // Project polygons onto the axis
    let min1 = Infinity
    let max1 = -Infinity
    let min2 = Infinity
    let max2 = -Infinity

    // Project first polygon
    for (const vertex of p1.vertices()) {
      const projection = vertex.x * axis.x + vertex.y * axis.y
      min1 = Math.min(min1, projection)
      max1 = Math.max(max1, projection)
    }

    // Project second polygon
    for (const vertex of p2.vertices()) {
      const projection = vertex.x * axis.x + vertex.y * axis.y
      min2 = Math.min(min2, projection)
      max2 = Math.max(max2, projection)
    }

    // Check for separation
    if (max1 < min2 || max2 < min1) {
      return null // Separated along this axis
    }

    // Calculate overlap depth and contact information
    const overlap1 = max1 - min2
    const overlap2 = max2 - min1
    const depth = Math.min(overlap1, overlap2)

    // Determine contact normal (always point from p1 to p2)
    let normal = axis
    if (overlap1 < overlap2) {
      normal = VectorUtils.inverted(axis)
    }

    // Calculate overlap region
    const overlapStart = Math.max(min1, min2)
    const overlapEnd = Math.min(max1, max2)

    // Find vertices that contribute to the overlap region
    const overlappingVertices: Position[] = []
    for (const vertex of p1.vertices()) {
      const projection = vertex.x * axis.x + vertex.y * axis.y
      if (projection >= overlapStart && projection <= overlapEnd) {
        overlappingVertices.push(vertex)
      }
    }
    for (const vertex of p2.vertices()) {
      const projection = vertex.x * axis.x + vertex.y * axis.y
      if (projection >= overlapStart && projection <= overlapEnd) {
        overlappingVertices.push(vertex)
      }
    }

    // Calculate the approximate center of the overlapping region
    let contactPosition: Position = { x: 0, y: 0 }
    if (overlappingVertices.length > 0) {
      let sumX = 0
      let sumY = 0
      for (const vertex of overlappingVertices) {
        sumX += vertex.x
        sumY += vertex.y
      }
      contactPosition = {
        x: sumX / overlappingVertices.length,
        y: sumY / overlappingVertices.length
      }
    }

    return Contact.withNormal(depth, VectorUtils.inverted(normal), contactPosition)
  }

  private static getPointAndSegmentContact(position: Position, segment: Segment, checkSide: boolean): Contact | null {
    const projection = GeometryUtils.getPointToLineProjection(position, segment)
    const normal = segment.normal()
    const direction = VectorUtils.vectorFromTo(projection, position)
    VectorUtils.normalize(direction)
    
    if (checkSide && VectorUtils.dotProduct(direction, normal) < 0) {
      return null
    }

    return Contact.withNormal(
      BattleUtils.distance(position, projection),
      normal,
      new Segment(position, projection).center()
    )
  }

  private static isPointOnInnerSideOfSegment(position: Position, segment: Segment): boolean {
    const projection = GeometryUtils.getPointToLineProjection(position, segment)
    const normal = segment.normal()
    const direction = VectorUtils.vectorFromTo(projection, position)
    VectorUtils.normalize(direction)
    return VectorUtils.dotProduct(direction, normal) > 0
  }

  private static innerDistanceFromPointToSegment(position: Position, segment: Segment): number {
    const projection = GeometryUtils.getPointToSegmentProjection(position, segment)
    if (!projection) {
      return 0
    }

    const normal = segment.normal()
    const direction = VectorUtils.vectorFromTo(projection, position)
    VectorUtils.normalize(direction)
    
    if (VectorUtils.dotProduct(direction, normal) < 0) {
      return 0
    }

    return BattleUtils.distance(position, projection)
  }

  private static isHalfCircleBottomAndOtherHalfCircleTopContact(bottom: Segment, otherHalfCircle: HalfCircle): boolean {
    const intersectionPoints = GeometryUtils.getSegmentAndCircleIntersectionPoints(bottom, otherHalfCircle.circle())
    
    for (const intersectionPoint of intersectionPoints) {
      const pointAngle = VectorUtils.angleFromTo(otherHalfCircle.center, intersectionPoint)
      if (pointAngle > otherHalfCircle.angle && pointAngle < otherHalfCircle.angle + Math.PI) {
        return true
      }
    }

    return false
  }

  private static getSegmentAndCircleContact(surface: Segment, circle: Circle): Contact | null {
    const projection = GeometryUtils.getPointToSegmentProjection(circle.center, surface)
    if (!projection) {
      return null
    }

    const distance = BattleUtils.distance(projection, circle.center)
    if (distance > circle.radius) {
      return null
    }

    const direction = VectorUtils.vectorFromTo(circle.center, projection)
    const surfaceNormal = surface.normal()
    const depth = VectorUtils.dotProduct(direction, surfaceNormal) > 0
      ? circle.radius - distance
      : circle.radius + distance

    return Contact.withNormal(depth, VectorUtils.inverted(surfaceNormal), projection)
  }

  private static getPointAndCircleContact(point: Position, circle: Circle): Contact | null {
    const distance = BattleUtils.distance(circle.center, point)
    const depth = distance < circle.radius ? circle.radius - distance : 0
    const normal = VectorUtils.vectorFromTo(point, circle.center)
    VectorUtils.normalize(normal)
    return Contact.withNormal(depth, normal, point)
  }

  private static getDeepestContact(contacts: Set<Contact>): Contact | null {
    let deepestContact: Contact | null = null
    for (const contact of contacts) {
      if (contact && (deepestContact === null || contact.depth > deepestContact.depth)) {
        deepestContact = contact
      }
    }
    return deepestContact
  }

  private static addContactToSetIfNotNull(contacts: Set<Contact>, contact: Contact | null) {
    contact && contacts.add(contact)
  }
}
