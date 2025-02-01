import type {BodyVector, Position, Vector} from '@/playground/data/common'

export const VectorUtils = {
  getVerticalProjection(vector: Vector, groundAngle: number): number {
    return -vector.x * Math.sin(groundAngle) + vector.y * Math.cos(groundAngle)
  },

  getHorizontalProjection(vector: Vector, groundAngle: number): number {
    return vector.x * Math.cos(groundAngle) + vector.y * Math.sin(groundAngle)
  },

  getComponentX(
    verticalProjection: number,
    horizontalProjection: number,
    groundAngle: number
  ): number {
    return (
      -verticalProjection * Math.sin(groundAngle) + horizontalProjection * Math.cos(groundAngle)
    )
  },

  getComponentY(
    verticalProjection: number,
    horizontalProjection: number,
    groundAngle: number
  ): number {
    return verticalProjection * Math.cos(groundAngle) + horizontalProjection * Math.sin(groundAngle)
  },

  sumOf(...vectors: Vector[]): Vector {
    let sumX = 0
    let sumY = 0
    for (const vector of vectors) {
      sumX += vector.x
      sumY += vector.y
    }
    return { x: sumX, y: sumY }
  },

  sumOfBody(...vectors: BodyVector[]): BodyVector {
    let sumX = 0
    let sumY = 0
    let sumAngle = 0
    for (const vector of vectors) {
      sumX += vector.x
      sumY += vector.y
      sumAngle += vector.angle
    }
    return { x: sumX, y: sumY, angle: sumAngle }
  },

  angleFromTo(from: Position, to: Position) {
    const dx = to.x - from.x;
    const dy = to.y - from.y;
    return Math.atan2(dy, dx);
  },

  getMagnitude(vector: Vector) {
    return Math.sqrt(Math.pow(vector.x, 2) + Math.pow(vector.y, 2));
  },

  getAngle(vector: Vector) {
    return Math.atan2(vector.y, vector.x)
  }
}
