import {type BodyPosition, type Contact, type Position, type Velocity, zeroVector} from "@/playground/data/common"
import type {BattleModel, BodyModel, BoxModel, DroneModel, RoomModel, VehicleModel} from "~/playground/data/model";
import type {Collision} from "~/playground/battle/collision/collision";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {GroundContactUtils} from "~/playground/utils/ground-contact-utils";
import {BodyUtils} from "~/playground/utils/body-utils";
import {Circle, VectorProjections} from "~/playground/data/geometry";
import {SurfaceContactUtils} from "~/playground/utils/surface-contact-utils";

export interface Calculations {
  getModel(): any

  getMass(): number

  getVelocity(): Velocity

  getKineticEnergy(): number

  calculateNextPosition(timeStepSecs: number): void

  applyNextPosition(): void

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

  abstract getMass(): number

  abstract getVelocity(): Velocity

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

  getKineticEnergy(): number {
    return this.getMass() * (Math.pow(this.getVelocity().x, 2) + Math.pow(this.getVelocity().y, 2)) / 2
  }
}

export interface BodyCalculations extends Calculations {
  model: BodyModel

  getModel(): BodyModel

  getGroundContacts(): Set<Contact>

  calculateAllGroundContacts(roomModel: RoomModel): void

  getNextPosition(): BodyPosition | undefined

  getGeometryPosition(): Position

  getGeometryNextPosition(): BodyPosition | undefined

  getMovingKineticEnergy(): number

  getRotatingKineticEnergy(): number
}

export abstract class BodyCalculationsBase extends CalculationsBase {
  model: BodyModel
  nextPosition?: BodyPosition
  groundContacts: Set<Contact> = new Set()

  protected constructor(bodyModel: BodyModel) {
    super();
    this.model = bodyModel
  }

  getModel(): BodyModel {
    return this.model
  }

  calculateNextPosition(timeStep: number): void {
    const position = this.model.state.position
    const velocity = this.model.state.velocity
    this.nextPosition = {
      x: position.x + velocity.x * timeStep,
      y: position.y + velocity.y * timeStep,
      angle: VectorUtils.normalizedAngle(position.angle + velocity.angle * timeStep),
    }
  }

  applyNextPosition(): void {
    if (this.nextPosition) {
      this.model.state.position = this.nextPosition
    }
  }

  applyNormalMoveToNextPosition(normalMove: number, angle: number): void {
    const move = new VectorProjections(angle, normalMove, 0).recoverPosition()
    if (this.nextPosition) {
      this.nextPosition.x += move.x
      this.nextPosition.y += move.y
    }
  }

  getNextPosition(): BodyPosition | undefined {
    return this.nextPosition
  }

  getGeometryPosition(): Position {
    return BodyUtils.getGeometryPosition(this.model)
  }

  getGeometryBodyPosition(): BodyPosition {
    return BodyUtils.getGeometryBodyPosition(this.model)
  }

  getGeometryNextPosition(): BodyPosition | undefined {
    if (this.nextPosition) {
      const shift = this.model.preCalc.centerOfMassShift
      return BattleUtils.shiftedBodyPosition(this.nextPosition, -shift.distance, shift.angle + this.nextPosition.angle)
    }
  }

  getMovingKineticEnergy(): number {
    const velocity = this.model.state.velocity
    const mass = this.model.preCalc.mass
    return mass * (Math.pow(velocity.x, 2) + Math.pow(velocity.y, 2)) / 2
  }

  getRotatingKineticEnergy(): number {
    return this.model.preCalc.momentOfInertia * Math.pow(this.model.state.velocity.angle, 2) / 2
  }

  getMass(): number {
    return this.model.preCalc.mass
  }

  getVelocity(): Velocity {
    return this.model.state.velocity
  }

  override getKineticEnergy(): number {
    return this.getMovingKineticEnergy() + this.getRotatingKineticEnergy()
  }

  getGroundContacts(): Set<Contact> {
    return this.groundContacts
  }
}

export enum WheelSign {
  RIGHT = -1,
  LEFT = 1
}

export class WheelCalculations implements BodyCalculations {
  sign: WheelSign
  model: VehicleModel
  vehicle: VehicleCalculations
  groundContact: Contact | null = null
  position: Position | undefined
  velocity: Velocity | undefined
  nextPosition: Position | undefined

  constructor(
      sign: WheelSign,
      vehicle: VehicleCalculations
  ) {
    this.sign = sign
    this.vehicle = vehicle
    this.model = vehicle.model
    this.calculatePositionByVehicle()
    this.recalculateVelocity()
  }

  applyNextPosition(): void {
    this.vehicle.applyNextPosition()
  }

  getModel(): VehicleModel {
    return this.vehicle.getModel()
  }

  getGroundContacts(): Set<Contact> {
    const contacts: Set<Contact> = new Set<Contact>()
    this.groundContact && contacts.add(this.groundContact)
    return contacts
  }

  calculateGroundContact(wheelRadius: number, roomModel: RoomModel): void {
    const circle = new Circle(this.position!, wheelRadius)
    let contact = GroundContactUtils.getCircleGroundContact(circle, roomModel, false)
    if (!contact) {
      const surfaceContacts = SurfaceContactUtils.getContacts(circle, roomModel, false)
      const filteredContacts = Array.from(surfaceContacts).filter(cnt => cnt.normal.y < 0)
      if (filteredContacts.length > 0) {
        filteredContacts.sort((a, b) => a.depth - b.depth)
        contact = filteredContacts[0] || null
      }
    }
    this.groundContact = contact
  }

  calculateAllGroundContacts(roomModel: RoomModel): void {
    this.vehicle.calculateAllGroundContacts(roomModel)
  }

  getNextPosition(): BodyPosition | undefined {
    throw new Error('Method not implemented.')
  }

  getGeometryPosition(): Position {
    throw new Error('Method not implemented.')
  }

  getGeometryNextPosition(): BodyPosition | undefined {
    throw new Error('Method not implemented.')
  }

  getMovingKineticEnergy(): number {
    return this.vehicle.getMovingKineticEnergy()
  }

  getRotatingKineticEnergy(): number {
    return this.vehicle.getRotatingKineticEnergy()
  }

  getMass(): number {
    return this.vehicle.getMass()
  }

  getVelocity(): Velocity {
    if (this.velocity) {
      return this.velocity
    }
    throw new Error('Velocity not calculated')
  }

  getKineticEnergy(): number {
    return this.vehicle.getKineticEnergy()
  }

  calculateNextPosition(timeStepSecs: number): void {
    this.vehicle.calculateNextPosition(timeStepSecs)
  }

  applyNormalMoveToNextPosition(normalMove: number, angle: number): void {
    this.vehicle.applyNormalMoveToNextPosition(normalMove, angle)
  }

  getLastCollisions(): Set<Collision> {
    return this.vehicle.getLastCollisions()
  }

  getCollisions(iterationNumber: number): Set<Collision> {
    return this.vehicle.getCollisions(iterationNumber)
  }

  getAllCollisions(): Set<Collision> {
    return this.vehicle.getAllCollisions()
  }

  addCollisionsCheckedWith(id: number): void {
    return this.vehicle.addCollisionsCheckedWith(id)
  }

  clearCollisionsCheckedWith(): void {
    this.vehicle.clearCollisionsCheckedWith()
  }

  collisionsNotCheckedWith(id: number): boolean {
    return this.vehicle.collisionsNotCheckedWith(id)
  }

  recalculateVelocity() {
    const vehicleVelocity = this.vehicle.model.state.velocity
    const angle = this.vehicle.model.state.position.angle
    const wheelAngle = angle + Math.PI / 2 + this.sign * this.vehicle.model.preCalc.wheelAngle
    const wheelDistance = this.vehicle.model.preCalc.wheelDistance
    const wheelVelocity = VectorUtils.getPointVelocity(vehicleVelocity, wheelDistance, wheelAngle)
    if (!this.velocity) {
      this.velocity = zeroVector()
    }
    this.velocity.x = wheelVelocity.x
    this.velocity.y = wheelVelocity.y
  }

  calculatePositionByVehicle() {
    this.position = this.getWheelPosition(this.vehicle.model.state.position)
  }

  calculateNextPositionByVehicle() {
    if (this.vehicle.nextPosition) {
      this.nextPosition = this.getWheelPosition(this.vehicle.nextPosition)
    }
  }

  private getWheelPosition(vehiclePosition: BodyPosition): Position {
    const wheelDistance = this.vehicle.model.preCalc.wheelDistance
    const wheelAngle = this.vehicle.model.preCalc.wheelAngle
    return BattleUtils.shiftedPosition(vehiclePosition, -this.sign * wheelDistance,
        vehiclePosition.angle + this.sign * wheelAngle);
  }
}

export class VehicleCalculations extends BodyCalculationsBase implements BodyCalculations {
  override model: VehicleModel
  rightWheel: WheelCalculations
  leftWheel: WheelCalculations

  constructor(
      model: VehicleModel
  ) {
    super(model)
    this.model = model
    this.rightWheel = new WheelCalculations(WheelSign.RIGHT, this)
    this.leftWheel = new WheelCalculations(WheelSign.LEFT, this)
  }

  override getModel(): VehicleModel {
    return this.model
  }

  override getGroundContacts(): Set<Contact> {
    const allGroundContacts = new Set(this.groundContacts)
    if (this.rightWheel.groundContact) {
      allGroundContacts.add(this.rightWheel.groundContact)
    }
    if (this.leftWheel.groundContact) {
      allGroundContacts.add(this.leftWheel.groundContact)
    }
    return allGroundContacts
  }

  getTurretGroundContacts(): Set<Contact> {
    return this.groundContacts
  }

  calculateAllGroundContacts(roomModel: RoomModel): void {
    const position = this.getGeometryBodyPosition()
    const turretShape = this.model.specs.turretShape
    const bodyPart = BodyUtils.getBodyPart(turretShape, position)
    const contacts = GroundContactUtils.getContacts(bodyPart, roomModel, false)
    const surfaceContacts = SurfaceContactUtils.getContacts(bodyPart, roomModel, false)
    for (const contact of surfaceContacts) {
        if (contact.normal.y < 0) {
            contacts.add(contact)
        }
    }
    this.groundContacts = contacts
    const wheelRadius = this.model.specs.wheelRadius
    this.rightWheel.calculateGroundContact(wheelRadius, roomModel)
    this.leftWheel.calculateGroundContact(wheelRadius, roomModel)
  }

  override calculateNextPosition(timeStep: number): void {
    super.calculateNextPosition(timeStep)
    this.rightWheel.calculateNextPositionByVehicle()
    this.leftWheel.calculateNextPositionByVehicle()
  }
}

export class BoxCalculations extends BodyCalculationsBase implements BodyCalculations {
  override model: BoxModel

  constructor(model: BoxModel) {
    super(model);
    this.model = model
  }

  calculateAllGroundContacts(roomModel: RoomModel): void {
    const position = this.getGeometryBodyPosition()
    const shape = this.model.specs.shape
    const bodyPart = BodyUtils.getBodyPart(shape, position)
    const contacts = GroundContactUtils.getContacts(bodyPart, roomModel, false)
    const surfaceContacts = SurfaceContactUtils.getContacts(bodyPart, roomModel, false)
    for (const contact of surfaceContacts) {
      if (contact.normal.y < 0) {
        contacts.add(contact)
      }
    }
    this.groundContacts = contacts
  }
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
  boxes: BoxCalculations[]
  timeStepSecs: number

  constructor(
      model: BattleModel,
      vehicles: VehicleCalculations[],
      boxes: BoxCalculations[],
      timeStepSecs: number
  ) {
    this.model = model
    this.vehicles = vehicles
    this.boxes = boxes
    this.timeStepSecs = timeStepSecs
  }

  getMovingObjects(): Calculations[] {
    return [...this.vehicles, ...this.boxes]
  }
}

export function isBodyCalculationsImplementation(calculation: Calculations | undefined) {
  return calculation instanceof WheelCalculations
      || calculation instanceof VehicleCalculations
      || calculation instanceof BoxCalculations
}
