import type {Acceleration, Position, Velocity} from "@/data/common"

export enum WheelSign {
  RIGHT = -1,
  LEFT = 1
}

export enum WheelGroundState {
  FULL_OVER_GROUND = 'FULL_OVER_GROUND',
  HALF_OVER_GROUND = 'HALF_OVER_GROUND',
  HALF_UNDER_GROUND = 'HALF_UNDER_GROUND',
  FULL_UNDER_GROUND = 'FULL_UNDER_GROUND'
}

export interface NearestGroundPoint {
  position: Position
  distance: number
  index: number
}

export interface WheelCalculations {
  sign: WheelSign
  groundState: WheelGroundState
  position: Position | undefined
  velocity: Velocity | undefined
  nearestGroundPointByX: Position | undefined
  nearestGroundPoint: NearestGroundPoint | undefined
  groundAngle: number | undefined
  groundDepth: number | undefined
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
