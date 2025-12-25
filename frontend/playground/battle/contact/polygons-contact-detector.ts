import {type Polygon} from "~/playground/data/geometry";
import {Contact, type Position, type Vector} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";

export interface PolygonsContactDetector {
  detect: (p1: Polygon, p2: Polygon) => Contact | null,
}

export class SATPolygonsContactDetector implements PolygonsContactDetector {
  detect(p1: Polygon, p2: Polygon): Contact | null {
    let bestContact: Contact | null = null
    bestContact = this.checkPolygonAxes(p1, p2, bestContact, false)
    if (bestContact === null) {
      return null
    }
    bestContact = this.checkPolygonAxes(p2, p1, bestContact, true)
    return bestContact
  }

  private checkPolygonAxes(sourcePolygon: Polygon, targetPolygon: Polygon, bestContact: Contact | null, invert: boolean): Contact | null {
    for (const side of sourcePolygon.sides()) {
      const contact = this.checkSeparatingAxis(sourcePolygon, targetPolygon, side.normal())
      if (contact === null) {
        return null
      }
      if (bestContact === null || contact.depth < bestContact.depth) {
        bestContact = invert ? contact.inverted() : contact
      }
    }
    return bestContact
  }

  private checkSeparatingAxis(p1: Polygon, p2: Polygon, axis: Vector): Contact | null {
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
}
