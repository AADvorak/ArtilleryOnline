import {Collision, type CollisionsDetector} from "~/playground/battle/collision/collision";
import {BattleCalculations, BoxCalculations, type Calculations} from "~/playground/data/calculations";
import {Trapeze} from "~/playground/data/geometry";
import type {TrapezeShape} from "~/playground/data/shapes";
import {GroundContactUtils} from "~/playground/utils/ground-contact-utils";
import {Contact, zeroVector} from "~/playground/data/common";

export class BoxGroundCollisionsDetector implements CollisionsDetector {
  detect(calculations: Calculations, battle: BattleCalculations): Set<Collision> {
    if (calculations instanceof BoxCalculations) {
      return this.detectBox(calculations as BoxCalculations, battle)
    }
    return new Set<Collision>()
  }

  private detectBox(box: BoxCalculations, battle: BattleCalculations): Set<Collision> {
    const collisions = new Set<Collision>()
    const wallCollision = this.detectWallCollision(box, battle)

    if (wallCollision) {
      collisions.add(wallCollision)
    }

    const groundCollisions = this.detectGroundCollisions(box, battle)
    groundCollisions.forEach(collision => collisions.add(collision))

    return collisions
  }

  private detectGroundCollisions(box: BoxCalculations, battle: BattleCalculations): Set<Collision> {
    const trapeze = new Trapeze(box.getGeometryNextPosition()!, box.model.specs.shape as TrapezeShape)
    const contacts = GroundContactUtils.getTrapezeGroundContacts(trapeze, battle.model.room, true)

    const result = new Set<Collision>()
    contacts.forEach(contact => {
      result.add(Collision.withGround(box, contact))
    })

    return result
  }

  private detectWallCollision(box: BoxCalculations, battle: BattleCalculations): Collision | null {
    const nextX = box.getNextPosition()!.x
    const maxRadius = box.model.preCalc.maxRadius
    const xMax = battle.model.room.specs.rightTop.x
    const xMin = battle.model.room.specs.leftBottom.x

    if (nextX + maxRadius < xMax && nextX - maxRadius > xMin) {
      return null
    }

    const trapeze = new Trapeze(box.getGeometryNextPosition()!, box.model.specs.shape as TrapezeShape)
    const edges = [
      trapeze.bottomLeft(),
      trapeze.bottomRight(),
      trapeze.topRight(),
      trapeze.topLeft()
    ]

    const rightWallDepths = edges.map(edge => ({
      point: edge,
      depth: edge.x - xMax
    }))

    const rightWallDepth = rightWallDepths.reduce((max, current) =>
            current.depth > max.depth ? current : max,
        { point: zeroVector(), depth: 0 }
    )

    const rightWallContact = Contact.withAngle(rightWallDepth.depth, Math.PI / 2, rightWallDepth.point)
    if (rightWallContact) {
      return Collision.withWall(box, rightWallContact)
    }

    const leftWallDepths = edges.map(edge => ({
      point: edge,
      depth: xMin - edge.x
    }))

    const leftWallDepth = leftWallDepths.reduce((max, current) =>
            current.depth > max.depth ? current : max,
        { point: zeroVector(), depth: 0 }
    )

    const leftWallContact = Contact.withAngle(leftWallDepth.depth, -Math.PI / 2, leftWallDepth.point)
    if (leftWallContact) {
      return Collision.withWall(box, leftWallContact)
    }

    return null
  }
}
