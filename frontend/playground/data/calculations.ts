import type {BodyPosition, Contact, Position, Velocity} from "@/playground/data/common"
import type {BodyModel, BoxModel, DroneModel, VehicleModel} from "~/playground/data/model";

export interface Calculations {
  model: any
  getMass(): number
  getVelocity(): Velocity
  getKineticEnergy(): number
  calculateNextPosition(timeStepSecs: number): void
  applyNormalMoveToNextPosition(normalMove: number, angle: number): void
}

export class BodyCalculations implements Calculations {
  model: BodyModel
  groundContacts?: Set<Contact>
  nextPosition?: BodyPosition

  constructor(model: BodyModel) {
    this.model = model
  }

  calculateNextPosition(timeStepSecs: number): void {
    // todo implement
  }

  applyNormalMoveToNextPosition(normalMove: number, angle: number): void {
    // todo implement
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

export class VehicleCalculations extends BodyCalculations {
  override model: VehicleModel
  rightWheel: WheelCalculations
  leftWheel: WheelCalculations

  constructor(
      model: VehicleModel,
      rightWheel: WheelCalculations,
      leftWheel: WheelCalculations
  ) {
    super(model)
    this.model = model
    this.rightWheel = rightWheel
    this.leftWheel = leftWheel
  }
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
