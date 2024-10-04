import type {Acceleration, Position, Velocity} from "@/data/common"

export enum WheelSign {
  RIGHT = -1,
  LEFT = 1
}

export interface NearestGroundPoint {
  position: Position
  distance: number
  index: number
}

export interface WheelCalculations {
  sign: WheelSign
  position: Position | undefined
  velocity: Velocity | undefined
  nearestGroundPointByX: Position | undefined
  nearestGroundPoint: NearestGroundPoint | undefined
  groundAngle: number | undefined
  depth: number | undefined
  gravityAcceleration: Acceleration
  groundReactionAcceleration: Acceleration
  groundFrictionAcceleration: Acceleration
  engineAcceleration: Acceleration
  sumAcceleration: Acceleration | undefined
}

export interface VehicleCalculations {
  nextPosition: Position | undefined
  nextAngle: number | undefined
  rightWheel: WheelCalculations
  leftWheel: WheelCalculations
}
