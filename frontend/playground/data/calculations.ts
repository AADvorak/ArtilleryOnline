import type {BodyPosition, Contact, Position, Velocity} from "@/playground/data/common"
import type {BattleModel, BodyModel, BoxModel, DroneModel, VehicleModel} from "~/playground/data/model";
import type {Collision} from "~/playground/battle/collision/collision";

export interface Calculations {
  model: any
  getMass(): number
  getVelocity(): Velocity
  getKineticEnergy(): number
  calculateNextPosition(timeStepSecs: number): void
  applyNormalMoveToNextPosition(normalMove: number, angle: number): void
  getLastCollisions(): Set<Collision>
  getCollisions(iterationNumber: number): Set<Collision>
  getAllCollisions(): Set<Collision>
  addCollisionsCheckedWith(id: number): void
  clearCollisionsCheckedWith(): void
  collisionsNotCheckedWith(id: number): boolean
}

export abstract class CalculationsBase {
  private readonly collisionsCheckedWith: Set<number> = new Set()
  private readonly collisionMap: Map<number, Set<Collision>> = new Map()

  public getLastCollisions(): Set<Collision> {
    const maxKey = Math.max(...Array.from(this.collisionMap.keys()), 1)
    return this.getCollisions(maxKey)
  }

  public getCollisions(iterationNumber: number): Set<Collision> {
    if (!this.collisionMap.has(iterationNumber)) {
      this.collisionMap.set(iterationNumber, new Set())
    }
    return this.collisionMap.get(iterationNumber) as Set<Collision>
  }

  public getAllCollisions(): Set<Collision> {
    const result = new Set<Collision>()
    for (const collisions of this.collisionMap.values()) {
      for (const collision of collisions) {
        result.add(collision)
      }
    }
    return result
  }

  public addCollisionsCheckedWith(id: number): void {
    this.collisionsCheckedWith.add(id)
  }

  public clearCollisionsCheckedWith(): void {
    this.collisionsCheckedWith.clear()
  }

  public collisionsNotCheckedWith(id: number): boolean {
    return !this.collisionsCheckedWith.has(id)
  }
}

export class BodyCalculations extends CalculationsBase implements Calculations {
  model: BodyModel
  groundContacts?: Set<Contact>
  nextPosition?: BodyPosition

  constructor(model: BodyModel) {
    super()
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

export class BattleCalculations {
  model: BattleModel
  vehicles: VehicleCalculations[]
  timeStepSecs: number

  constructor(
      model: BattleModel,
      vehicles: VehicleCalculations[],
      timeStepSecs: number
  ) {
    this.model = model
    this.vehicles = vehicles
    this.timeStepSecs = timeStepSecs
  }

  getMovingObjects(): Calculations[] {
    return [...this.vehicles]
  }
}
