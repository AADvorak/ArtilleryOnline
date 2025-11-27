import {Collision, type CollisionsDetector} from "~/playground/battle/collision/collision";
import {
  BattleCalculations,
  type Calculations,
  VehicleCalculations,
  type WheelCalculations
} from "~/playground/data/calculations";
import {Circle, HalfCircle, Trapeze} from "~/playground/data/geometry";
import {GroundContactUtils} from "~/playground/utils/ground-contact-utils";
import {type HalfCircleShape, ShapeNames, type TrapezeShape} from "~/playground/data/shapes";
import {Contact} from "~/playground/data/common";

export class VehicleGroundCollisionsDetector implements CollisionsDetector {
  detect(calculations: Calculations, battle: BattleCalculations): Set<Collision> {
    if (calculations instanceof VehicleCalculations) {
      return this.detectForVehicle(calculations as VehicleCalculations, battle)
    }
    return new Set()
  }

  private detectForVehicle(vehicle: VehicleCalculations, battle: BattleCalculations): Set<Collision> {
    const collisions = new Set<Collision>()

    const wheels = [vehicle.rightWheel, vehicle.leftWheel]

    for (const wheel of wheels) {
      const groundCollision = this.detectWheelGroundCollision(wheel, battle)
      if (groundCollision) {
        collisions.add(groundCollision)
      }

      const wallCollision = this.detectWheelWallCollision(wheel, battle)
      if (wallCollision) {
        collisions.add(wallCollision)
      }
    }

    const hullCollisions = this.detectHullGroundCollisions(vehicle, battle)
    hullCollisions.forEach(collision => collisions.add(collision))

    return collisions
  }

  private detectWheelGroundCollision(wheel: WheelCalculations, battle: BattleCalculations): Collision | null {
    const circle = new Circle(wheel.nextPosition!, wheel.getModel().specs.wheelRadius)
    const contact = GroundContactUtils.getCircleGroundContact(circle, battle.model.room, true)

    if (contact === null) {
      return null
    }

    return Collision.withGround(wheel, contact)
  }

  private detectHullGroundCollisions(vehicle: VehicleCalculations, battle: BattleCalculations): Set<Collision> {
    let contacts: Set<Contact> = new Set()
    const position = vehicle.getGeometryBodyPosition()
    const roomModel = battle.model.room
    const turretShape = vehicle.model.specs.turretShape
    if (turretShape.name === ShapeNames.HALF_CIRCLE) {
      contacts = GroundContactUtils.getHalfCircleGroundContacts(
          HalfCircle.of(position, (turretShape as HalfCircleShape).radius),
          roomModel,true
      )
    }
    if (turretShape.name === ShapeNames.TRAPEZE) {
      contacts = GroundContactUtils.getTrapezeGroundContacts(
          new Trapeze(position, turretShape as TrapezeShape),
          roomModel, true
      )
    }

    const collisions = new Set<Collision>()
    for (const contact of contacts) {
      collisions.add(Collision.withGround(vehicle, contact))
    }

    return collisions
  }

  private detectWheelWallCollision(wheel: WheelCalculations, battle: BattleCalculations): Collision | null {
    const wheelRadius = wheel.vehicle.model.specs.wheelRadius
    const xMax = battle.model.room.specs.rightTop.x
    const xMin = battle.model.room.specs.leftBottom.x
    const nextPosition = wheel.nextPosition!

    const rightWallDepth = nextPosition.x + wheelRadius - xMax
    const rightWallContact = Contact.withAngle(rightWallDepth, Math.PI / 2,
        {x: xMax, y: nextPosition.y})

    if (rightWallContact !== null) {
      return Collision.withWall(wheel, rightWallContact)
    }

    const leftWallDepth = xMin - nextPosition.x + wheelRadius
    const leftWallContact = Contact.withAngle(leftWallDepth, -Math.PI / 2,
        {x: xMin, y: nextPosition.y})

    if (leftWallContact !== null) {
      return Collision.withWall(wheel, leftWallContact)
    }

    return null
  }
}
