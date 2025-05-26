import type {Acceleration, BodyPosition, Position, Velocity} from "@/playground/data/common"
import type {BodyModel, DroneModel} from "~/playground/data/model";

export interface BodyCalculations {
  model: BodyModel
}

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
  groundState: WheelGroundState | undefined
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
  jetAcceleration: Acceleration
  sumAcceleration: Acceleration | undefined
}

export interface VehicleCalculations {
  nextPosition: BodyPosition | undefined
  rightWheel: WheelCalculations
  leftWheel: WheelCalculations
}

export interface DroneCalculations {
  model: DroneModel
  target?: DroneTargetCalculations
}

export interface DroneTargetCalculations {
  xDiff: number
  angleDiff: number
}
