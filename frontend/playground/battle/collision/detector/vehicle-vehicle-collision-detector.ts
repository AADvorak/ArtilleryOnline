import {Collision, type CollisionsDetector} from "~/playground/battle/collision/collision";
import {
  BattleCalculations,
  type BodyCalculations,
  type Calculations,
  VehicleCalculations
} from "~/playground/data/calculations";
import {type BodyPart, Circle} from "~/playground/data/geometry";
import {ContactUtils} from "~/playground/utils/contact-utils";
import {VehicleUtils} from "~/playground/utils/vehicle-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";

export class VehicleVehicleCollisionsDetector implements CollisionsDetector {
  detect(calculations: Calculations, battle: BattleCalculations, first: boolean = false): Set<Collision> {
    if (calculations instanceof VehicleCalculations) {
      return this.detectForVehicle(calculations as VehicleCalculations, battle, first)
    }
    return new Set()
  }

  private detectForVehicle(vehicle: VehicleCalculations, battle: BattleCalculations, first: boolean): Set<Collision> {
    const collisions = new Set<Collision>()
    const otherVehicles = Array.from(battle.vehicles.values())
      .filter(otherVehicle =>
        otherVehicle.getModel().id !== vehicle.getModel().id &&
        otherVehicle.collisionsNotCheckedWith(vehicle.getModel().id)
      )
    
    const wheelRadius = vehicle.model.specs.wheelRadius
    const maxRadius = vehicle.model.preCalc.maxRadius
    const position = vehicle.getGeometryBodyPosition()
    const rightWheelPosition = vehicle.rightWheel.nextPosition!
    const leftWheelPosition = vehicle.leftWheel.nextPosition!
    
    const parts = new Map<BodyPart, BodyCalculations>([
      [VehicleUtils.getTurretBodyPart(vehicle.model.specs.turretShape, position), vehicle],
      [new Circle(rightWheelPosition, wheelRadius), vehicle.rightWheel],
      [new Circle(leftWheelPosition, wheelRadius), vehicle.leftWheel]
    ])

    for (const otherVehicle of otherVehicles) {
      vehicle.addCollisionsCheckedWith(otherVehicle.getModel().id)
      otherVehicle.addCollisionsCheckedWith(vehicle.getModel().id)
      
      const otherMaxRadius = otherVehicle.model.preCalc.maxRadius
      const otherPosition = otherVehicle.getGeometryBodyPosition()
      
      if (BattleUtils.distance(otherPosition, position) > maxRadius + otherMaxRadius) {
        continue
      }

      const otherWheelRadius = otherVehicle.model.specs.wheelRadius
      const otherLeftWheelPosition = otherVehicle.leftWheel.nextPosition!
      const otherRightWheelPosition = otherVehicle.rightWheel.nextPosition!
      
      const otherParts = new Map<BodyPart, BodyCalculations>([
        [VehicleUtils.getTurretBodyPart(otherVehicle.model.specs.turretShape, otherPosition), otherVehicle],
        [new Circle(otherLeftWheelPosition, otherWheelRadius), otherVehicle.leftWheel],
        [new Circle(otherRightWheelPosition, otherWheelRadius), otherVehicle.rightWheel]
      ])

      for (const [part, partValue] of parts) {
        for (const [otherPart, otherPartValue] of otherParts) {
          const contact = ContactUtils.getBodyPartsContact(part, otherPart)
          if (contact !== null) {
            collisions.add(Collision.withVehicle(partValue, otherPartValue, contact))
            if (first) return collisions
          }
        }
      }
    }

    return collisions
  }
}
