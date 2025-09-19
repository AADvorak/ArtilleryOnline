import type {BodyVector, BodyVelocity, Position, Vector} from '@/playground/data/common'

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
    return {x: sumX, y: sumY}
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
    return {x: sumX, y: sumY, angle: sumAngle}
  },

  sumOfBodyArr(vectors: BodyVector[]): BodyVector {
    let sumX = 0
    let sumY = 0
    let sumAngle = 0
    for (const vector of vectors) {
      sumX += vector.x
      sumY += vector.y
      sumAngle += vector.angle
    }
    return {x: sumX, y: sumY, angle: sumAngle}
  },

  angleFromTo(from: Position, to: Position) {
    const dx = to.x - from.x;
    const dy = to.y - from.y;
    return Math.atan2(dy, dx);
  },

  vectorFromTo(from: Position, to: Position): Vector {
    return {
      x: to.x - from.x,
      y: to.y - from.y
    }
  },

  getMagnitude(vector: Vector) {
    return Math.sqrt(Math.pow(vector.x, 2) + Math.pow(vector.y, 2));
  },

  getBodyMagnitude(vector: BodyVector) {
    return Math.sqrt(Math.pow(vector.x, 2) + Math.pow(vector.y, 2) + Math.pow(vector.angle, 2));
  },

  normalize(vector: Vector) {
    const magnitude = this.getMagnitude(vector)
    vector.x /= magnitude
    vector.y /= magnitude
  },

  getAngle(vector: Vector) {
    return Math.atan2(vector.y, vector.x)
  },

  getNormal(angle: number): Vector {
    return {
      x: Math.cos(angle - Math.PI / 2),
      y: Math.sin(angle - Math.PI / 2)
    }
  },

  getPointVelocity(bodyVelocity: BodyVelocity, pointDistance: number, pointAngle: number) {
    const angleVelocity = bodyVelocity.angle * pointDistance
    return {
      x: bodyVelocity.x - angleVelocity * Math.sin(pointAngle),
      y: bodyVelocity.y + angleVelocity * Math.cos(pointAngle)
    }
  },

  dotProduct(v1: Vector, v2: Vector): number {
    return v1.x * v2.x + v1.y * v2.y
  },

  vectorProduct(v1: Vector, v2: Vector): number {
    return v1.x * v2.y - v1.y * v1.x
  },

  projectionOfOnto(of: Vector, vector: Vector): Vector {
    const dotProduct = this.dotProduct(of, vector)
    const magnitudeSquared = Math.pow(this.getMagnitude(vector), 2)
    if (magnitudeSquared == 0) {
      return {
        x: of.x,
        y: of.y
      }
    }
    const scalar = dotProduct / magnitudeSquared
    return {
      x: scalar * vector.x,
      y: scalar * vector.y
    }
  },

  multiply(vector: Vector, scalar: number): Vector {
    return {
      x: vector.x * scalar,
      y: vector.y * scalar
    }
  },

  tangential(angle: number): Vector {
    return {
      x: Math.cos(angle),
      y: Math.sin(angle)
    }
  }
}
