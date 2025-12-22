import {Collision, type CollisionsDetector} from "~/playground/battle/collision/collision"
import {BattleCalculations, BoxCalculations, type Calculations} from "~/playground/data/calculations"
import {ContactUtils} from "~/playground/utils/contact-utils"
import {BattleUtils} from "~/playground/utils/battle-utils"
import {BodyUtils} from "~/playground/utils/body-utils"

export class BoxBoxCollisionsDetector implements CollisionsDetector {
  detect(calculations: Calculations, battle: BattleCalculations): Set<Collision> {
    if (calculations instanceof BoxCalculations) {
      return this.detectForBox(calculations, battle)
    }
    return new Set()
  }

  private detectForBox(box: BoxCalculations, battle: BattleCalculations): Set<Collision> {
    const collisions = new Set<Collision>()
    const otherBoxes = battle.boxes.filter(value => 
      value.getModel().id !== box.getModel().id && value.collisionsNotCheckedWith(box.getModel().id)
    )
    const maxRadius = box.model.preCalc.maxRadius
    const position = box.getNextPosition()!
    const geometryPosition = box.getGeometryNextPosition()!
    const bodyPart = BodyUtils.getBodyPart(box.model.specs.shape, geometryPosition)
    
    for (const otherBox of otherBoxes) {
      box.addCollisionsCheckedWith(otherBox.getModel().id)
      otherBox.addCollisionsCheckedWith(box.getModel().id)
      const otherMaxRadius = otherBox.model.preCalc.maxRadius
      const otherPosition = otherBox.getNextPosition()!
      const otherGeometryPosition = otherBox.getGeometryNextPosition()!
      
      if (BattleUtils.distance(position, otherPosition) > maxRadius + otherMaxRadius) {
        continue
      }
      
      const otherBodyPart = BodyUtils.getBodyPart(otherBox.model.specs.shape, otherGeometryPosition)
      const contact = ContactUtils.getBodyPartsContact(bodyPart, otherBodyPart)
      if (contact !== null) {
        collisions.add(Collision.withBox(box, otherBox, contact))
      }
    }
    
    return collisions
  }
}
