import type {BodyPosition, Contact, Position, Velocity} from "@/playground/data/common"
import type {BodyModel, BoxModel, DroneModel, VehicleModel} from "~/playground/data/model";

export interface BodyCalculations {
  model: BodyModel
  groundContacts?: Set<Contact>
}

export enum WheelSign {
  RIGHT = -1,
  LEFT = 1
}

export interface WheelCalculations {
  sign: WheelSign
  groundContact: Contact | null
  position: Position | undefined
  velocity: Velocity | undefined
}

export interface VehicleCalculations extends BodyCalculations {
  model: VehicleModel
  nextPosition: BodyPosition | undefined
  rightWheel: WheelCalculations
  leftWheel: WheelCalculations
}

export interface BoxCalculations extends BodyCalculations {
  model: BoxModel
}

export interface DroneCalculations {
  model: DroneModel
  target?: DroneTargetCalculations
}

export interface DroneTargetCalculations {
  xDiff: number
  angleDiff: number
}
