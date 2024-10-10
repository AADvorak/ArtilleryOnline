import type { Vector } from '@/playground/data/common'

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
  }
}
