import {Collision, type CollisionsDetector} from "~/playground/battle/collision/collision";
import {BattleCalculations, BoxCalculations, type Calculations} from "~/playground/data/calculations";
import {SurfaceContactUtils} from "~/playground/utils/surface-contact-utils";
import {BodyUtils} from "~/playground/utils/body-utils";

export class BoxSurfaceCollisionsDetector implements CollisionsDetector {
  detect(calculations: Calculations, battle: BattleCalculations): Set<Collision> {
    if (calculations instanceof BoxCalculations) {
      return this.detectBoxCollisions(calculations, battle)
    }
    return new Set()
  }

  private detectBoxCollisions(box: BoxCalculations, battle: BattleCalculations): Set<Collision> {
    const collisions = new Set<Collision>()
    const bodyPart = BodyUtils.getBodyPart(box.model.specs.shape, box.getGeometryNextPosition()!)
    const contacts = SurfaceContactUtils.getContacts(bodyPart, battle.model.room, true)
    for (const contact of contacts) {
      collisions.add(Collision.withSurface(box, contact))
    }
    return collisions
  }
}
