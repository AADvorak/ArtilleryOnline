import type {BodyPosition, Contact, Position, Velocity} from "@/playground/data/common"
import type {BodyModel, BoxModel, DroneModel, VehicleModel} from "~/playground/data/model";

export interface Calculations {
  model: any
  getMass(): number
  getVelocity(): Velocity
  getKineticEnergy(): number
}

export class BodyCalculations implements Calculations{
  model: BodyModel
  groundContacts?: Set<Contact>

  constructor(model: BodyModel) {
    this.model = model
  }

  getMass(): number {
    return this.model.preCalc.mass
  }

  getVelocity(): Velocity {
    return this.model.state.velocity
  }

  getKineticEnergy(): number {
    return this.getMass() * (Math.pow(this.getVelocity().x, 2) + Math.pow(this.getVelocity().y, 2)) / 2
  }
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

export interface DroneCalculations extends Calculations {
  model: DroneModel
  target?: DroneTargetCalculations
}

export interface DroneTargetCalculations {
  xDiff: number
  angleDiff: number
}
