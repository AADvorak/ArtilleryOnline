import {Collision, type CollisionsDetector} from "~/playground/battle/collision/collision"
import {
  BattleCalculations, type BodyCalculations,
  BoxCalculations,
  type Calculations
} from "~/playground/data/calculations"
import {type BodyPart, Circle} from "~/playground/data/geometry"
import {ContactUtils} from "~/playground/utils/contact-utils"
import {BattleUtils} from "~/playground/utils/battle-utils"
import {BodyUtils} from "~/playground/utils/body-utils"

export class BoxVehicleCollisionsDetector implements CollisionsDetector {
  detect(calculations: Calculations, battle: BattleCalculations): Set<Collision> {
    if (calculations instanceof BoxCalculations) {
      return this.detectForBox(calculations as BoxCalculations, battle)
    }
    return new Set()
  }

  private detectForBox(box: BoxCalculations, battle: BattleCalculations): Set<Collision> {
    const collisions = new Set<Collision>()
    const maxRadius = box.model.preCalc.maxRadius
    const bodyPart = BodyUtils.getBodyPart(box.model.specs.shape, box.getGeometryNextPosition()!)
    
    for (const vehicle of battle.vehicles) {
      const otherMaxRadius = vehicle.model.preCalc.maxRadius
      const otherPosition = vehicle.getGeometryNextPosition()!
      
      if (BattleUtils.distance(otherPosition, box.getNextPosition()!) > maxRadius + otherMaxRadius) {
        continue
      }
      
      const otherWheelRadius = vehicle.model.specs.wheelRadius
      const otherLeftWheelPosition = vehicle.leftWheel.nextPosition!
      const otherRightWheelPosition = vehicle.rightWheel.nextPosition!
      
      const otherParts = new Map<BodyPart, BodyCalculations>([
        [BodyUtils.getBodyPart(vehicle.model.specs.turretShape, otherPosition), vehicle],
        [new Circle(otherLeftWheelPosition, otherWheelRadius), vehicle.leftWheel],
        [new Circle(otherRightWheelPosition, otherWheelRadius), vehicle.rightWheel]
      ])
      
      for (const [otherPart, otherPartValue] of otherParts) {
        const contact = ContactUtils.getBodyPartsContact(bodyPart, otherPart)
        if (contact !== null) {
          collisions.add(Collision.withVehicle(box, otherPartValue, contact))
        }
      }
    }
    
    return collisions
  }
}
