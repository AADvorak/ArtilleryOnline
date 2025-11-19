import type {BattleCalculations} from "~/playground/data/calculations";
import {
  Collision,
  type CollisionPreprocessor,
  type CollisionsDetector,
  type CollisionsPostprocessor
} from "~/playground/battle/collision/collision";
import type {CollisionResolver} from "~/playground/battle/collision/collision-resolver";
import {CollideObjectType} from "~/playground/data/common";

class CollisionsProcessor {
  private readonly logging: boolean
  private readonly additionalIterationsNumber: number
  private readonly detectors: CollisionsDetector[]
  private readonly preprocessors: CollisionPreprocessor[]
  private readonly postprocessors: CollisionsPostprocessor[]
  private readonly resolver: CollisionResolver

  constructor(
      logging: boolean,
      additionalIterationsNumber: number,
      detectors: CollisionsDetector[],
      preprocessors: CollisionPreprocessor[],
      postprocessors: CollisionsPostprocessor[],
      resolver: CollisionResolver
  ) {
    this.logging = logging
    this.additionalIterationsNumber = additionalIterationsNumber
    this.detectors = detectors
    this.preprocessors = preprocessors
    this.postprocessors = postprocessors
    this.resolver = resolver
  }

  public process(battle: BattleCalculations): void {
    this.processIteration(battle, 1)
    this.postprocessCollisions(battle)
  }

  public processIteration(battle: BattleCalculations, iterationNumber: number): void {
    this.detectAllCollisions(battle, iterationNumber)
    this.resolveStrongestCollisions(battle)

    if (this.collisionsExist(battle)) {
      if (iterationNumber - 1 < this.additionalIterationsNumber) {
        this.processIteration(battle, iterationNumber + 1)
      }
    }
  }

  private detectAllCollisions(battle: BattleCalculations, iterationNumber: number): void {
    battle.getMovingObjects().forEach(object => object.clearCollisionsCheckedWith())
    battle.getMovingObjects().forEach(object =>
        this.detectors.forEach(detector =>
            detector.detect(object, battle).forEach(collision =>
                object.getCollisions(iterationNumber).add(collision))))
  }

  private resolveStrongestCollisions(battle: BattleCalculations): void {
    battle.getMovingObjects().forEach(object => {
      const collision = this.findStrongestCollision(object.getLastCollisions())
      if (collision != null && (this.firstObjectIsProjectile(collision)
          || this.secondObjectDoesNotHaveGroundCollisions(collision))) {

        let shouldResolve = true
        for (const preprocessor of this.preprocessors) {
          const result = preprocessor.process(collision, battle)
          if (result != null) {
            shouldResolve = result
          }
        }

        if (shouldResolve) {
          this.resolver.resolve(collision, battle.model, battle.timeStepSecs)
        }
      }
    })
  }

  private postprocessCollisions(battle: BattleCalculations): void {
    battle.getMovingObjects().forEach(object =>
        this.postprocessors.forEach(postprocessor =>
            postprocessor.process(object, battle)))
  }

  private collisionsExist(battle: BattleCalculations): boolean {
    for (const object of battle.getMovingObjects()) {
      if (!!object.getLastCollisions().size) {
        return true
      }
    }
    return false
  }

  private findStrongestCollision(collisions: Set<Collision>): Collision | null {
    if (collisions.size === 0) {
      return null
    }

    let strongest: Collision | null = null

    for (const collision of collisions) {
      if (strongest === null) {
        strongest = collision
        continue
      }

      const collisionVelocity = collision.closingVelocity()
      const strongestVelocity = strongest.closingVelocity()

      if (CollideObjectType.GROUND === collision.type
          && CollideObjectType.GROUND !== strongest.type) {
        strongest = collision
      } else if (collisionVelocity < 1.0 && strongestVelocity < 1.0
          && collision.contact.depth > strongest.contact.depth) {
        strongest = collision
      } else if (collisionVelocity > strongestVelocity) {
        strongest = collision
      }
    }

    return strongest
  }

  private firstObjectIsProjectile(collision: Collision): boolean {
    // todo not implemented
    return false
  }

  private secondObjectDoesNotHaveGroundCollisions(collision: Collision): boolean {
    const second = collision.pair.second
    if (second == null) {
      return true
    }

    const secondCollisions = second.getLastCollisions()
    for (const secondCollision of secondCollisions) {
      if (CollideObjectType.GROUND === secondCollision.type) {
        return false
      }
    }

    return true
  }
}
