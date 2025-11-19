import type {Position} from "~/playground/data/common";
import type {Segment} from "~/playground/data/geometry";

export const GeometryUtils = {

  getPointToSegmentProjection(
      point: Position,
      segment: Segment
  ): Position | null {
    return this.getPointToSegmentOrLineProjection(point, segment, false)
  },

  getPointToLineProjection(
      point: Position,
      segment: Segment
  ): Position {
    return this.getPointToSegmentOrLineProjection(point, segment, true)!
  },

  getPointToSegmentOrLineProjection(
      point: Position,
      segment: Segment,
      notCheckInside: boolean
  ): Position | null {
    const A = point.x - segment.begin.x
    const B = point.y - segment.begin.y
    const C = segment.end.x - segment.begin.x
    const D = segment.end.y - segment.begin.y

    const dot = A * C + B * D
    const squareLength = C * C + D * D
    const param = squareLength !== 0 ? dot / squareLength : -1

    const projection: Position = {
      x: segment.begin.x + param * C,
      y: segment.begin.y + param * D
    }

    if (notCheckInside) {
      return projection
    }

    if (param >= 0 && param <= 1) {
      return projection
    }

    return null
  }
}
