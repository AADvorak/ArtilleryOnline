import {BodyCollisionData, type Collision, ComponentData} from "~/playground/battle/collision/collision";
import type {BattleModel, BodyModel} from "~/playground/data/model";
import {type Calculations, VehicleCalculations, WheelCalculations} from "~/playground/data/calculations";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {type BodyVelocity, cloneVector, type Contact, type Velocity} from "~/playground/data/common";
import {VectorProjections} from "~/playground/data/geometry";
import {BodyUtils} from "~/playground/utils/body-utils";

export class CollisionResolver {
  private static readonly FRICTION_VELOCITY_THRESHOLD = 0.1
  private readonly logging: boolean

  constructor(logging: boolean) {
    this.logging = logging
  }

  resolve(collision: Collision, battleModel: BattleModel, timeStepSecs: number): void {
    const kineticEnergyBefore = collision.sumKineticEnergy()

    if (this.logging) console.log('----------------Begin collision resolution------------------')
    if (this.logging) console.log(collision)
    if (this.logging) console.log(`before collision resolution energy = ${kineticEnergyBefore.toFixed(3)}`)

    const first = collision.pair.first
    const second = collision.pair.second

    let firstModel: BodyModel | undefined = undefined
    let secondModel: BodyModel | undefined = undefined

    const firstData = collision.bodyCollisionDataPair.first
    const secondData = collision.bodyCollisionDataPair.second

    if (first instanceof VehicleCalculations || first instanceof WheelCalculations) {
      firstModel = first.model
    }

    if (second instanceof VehicleCalculations || second instanceof WheelCalculations) {
      secondModel = second.model
    }

    if (collision.hit) {
      if (secondModel && second) {
        const hitDirection = cloneVector(first.getVelocity())
        VectorUtils.normalize(hitDirection)
        const hitData = BodyCollisionData.getComponentData(
            secondModel,
            hitDirection,
            collision.contact.position
        )

        const recalcMass = first.getMass() * hitData.resultMass / second.getMass()
        const impulseDelta = recalcMass * VectorUtils.getMagnitude(first.getVelocity())

        if (this.logging) {
          console.log(`Object id ${firstModel?.id} = hits object id = ${secondModel.id}]`)
          console.log(`${collision.contact}`)
          console.log(`HitData: ${hitData}`)
          console.log(`Mass = ${first.getMass().toFixed(3)}, recalcMass = ${recalcMass.toFixed(3)}`)
        }

        this.recalculateBodyVelocity(
            secondModel.state.velocity,
            collision.contact,
            hitData,
            impulseDelta,
            -1,
            false
        )

        const kineticEnergyAfter = second.getKineticEnergy()
        if (kineticEnergyAfter > kineticEnergyBefore) {
          const velocityMultiplier = Math.sqrt(kineticEnergyBefore / kineticEnergyAfter)
          this.multiplyBodyVelocity(secondModel.state.velocity, velocityMultiplier)
        }

        if (this.logging) console.log(`after collision resolution energy = ${second.getKineticEnergy().toFixed(3)}`)
        if (this.logging) console.log('----------------End hit resolution------------------')
      }
      return
    }

    if (this.logging) {
      if (secondModel) {
        console.log(`Collision of objects ids = [${firstModel?.id}, ${secondModel.id}]`)
      } else {
        console.log(`Collision with unmovable of object id = ${firstModel?.id}`)
      }
    }

    if (collision.getImpact() > 0) {
      if (this.logging) console.log('Closing resolving')

      if (firstModel && firstData) {
        this.recalculateBodyVelocity(
            firstModel.state.velocity,
            collision.contact,
            firstData.normalData,
            collision.getImpact(),
            1,
            false
        )
      } else {
        this.recalculatePointVelocity(
            first.getVelocity(),
            collision.contact,
            first.getMass(),
            collision.getImpact()
        )
      }

      if (second) {
        if (secondModel && secondData) {
          this.recalculateBodyVelocity(
              secondModel.state.velocity,
              collision.contact,
              secondData.normalData,
              collision.getImpact(),
              -1,
              false
          )
        } else {
          this.recalculatePointVelocity(
              second.getVelocity(),
              collision.contact,
              second.getMass(),
              collision.getImpact()
          )
        }
      }
    }

    let frictionVelocity = 0.0
    if (firstData) {
      frictionVelocity += firstData.velocityProjections.tangential
    }
    if (secondData) {
      frictionVelocity += secondData.velocityProjections.tangential
    }

    if (Math.abs(frictionVelocity) > CollisionResolver.FRICTION_VELOCITY_THRESHOLD) {
      this.resolveFrictionVelocity(
          collision,
          frictionVelocity,
          firstModel,
          secondModel,
          firstData,
          secondData,
          battleModel.room.specs.groundMaxDepth
      )
    }

    this.recalculatePositionsAndResolveInterpenetration(collision, timeStepSecs)

    const kineticEnergyAfter = collision.sumKineticEnergy()
    if (kineticEnergyAfter > kineticEnergyBefore) {
      const velocityMultiplier = Math.sqrt(kineticEnergyBefore / kineticEnergyAfter)

      if (firstModel) {
        this.multiplyBodyVelocity(firstModel.state.velocity, velocityMultiplier)
      } else {
        this.multiplyVelocity(first.getVelocity(), velocityMultiplier)
      }

      if (second) {
        if (secondModel) {
          this.multiplyBodyVelocity(secondModel.state.velocity, velocityMultiplier)
        } else {
          this.multiplyVelocity(second.getVelocity(), velocityMultiplier)
        }
      }
    }

    if (this.logging) console.log(`after collision resolution energy = ${collision.sumKineticEnergy().toFixed(3)}`)
    if (this.logging) console.log('----------------End collision resolution------------------')
  }

  private resolveFrictionVelocity(
      collision: Collision,
      frictionVelocity: number,
      firstModel: BodyModel | undefined,
      secondModel: BodyModel | undefined,
      firstData: BodyCollisionData | undefined,
      secondData: BodyCollisionData | undefined,
      groundMaxDepth: number
  ): void {
    if (!secondData && firstData && firstModel) {
      if (this.logging) console.log('Friction resolving')

      const depth = Math.min(collision.contact.depth, groundMaxDepth)
      const impulseDelta = frictionVelocity * depth * this.getFrictionCoefficient(collision.pair.first)

      this.recalculateBodyVelocity(
          firstModel.state.velocity,
          collision.contact,
          firstData.tangentialData,
          impulseDelta,
          1,
          true
      )
    }
  }

  private recalculateBodyVelocity(
      velocity: BodyVelocity,
      contact: Contact,
      componentData: ComponentData,
      impulseDelta: number,
      sign: number,
      tangential: boolean
  ): void {
    const velocityDelta = -sign * impulseDelta / componentData.resultMass
    const imc = componentData.inertiaToMassCoefficient

    if (this.logging) console.log(`velocityDelta = ${velocityDelta.toFixed(3)}`)

    if (componentData.inertiaToMassCoefficient > 0) {
      const angleVelocityDelta = componentData.rotationSign * imc * velocityDelta /
          componentData.distanceToAxis / (1 + imc)

      if (this.logging) console.log(`angleVelocityDelta = ${angleVelocityDelta.toFixed(3)}`)
      velocity.angle += angleVelocityDelta
    }

    if (componentData.inertiaToMassCoefficient < 1) {
      const movingVelocityDeltaMagnitude = velocityDelta / (1 + imc)
      const movingVelocityDelta = new VectorProjections(contact.angle,
          tangential ? 0.0 : movingVelocityDeltaMagnitude,
          tangential ? movingVelocityDeltaMagnitude : 0.0
      ).recoverVelocity()

      if (this.logging) console.log('movingVelocityDelta', movingVelocityDelta)
      velocity.x += movingVelocityDelta.x
      velocity.y += movingVelocityDelta.y
    }
  }

  private recalculatePointVelocity(
      velocity: Velocity,
      contact: Contact,
      mass: number,
      impulseDelta: number
  ): void {
    const velocityDelta = -impulseDelta / mass
    if (this.logging) console.log(`velocityDelta = ${velocityDelta.toFixed(3)}`)

    const movingVelocityDelta = new VectorProjections(contact.angle, velocityDelta, 0).recoverVelocity()
    if (this.logging) console.log(`movingVelocityDelta = ${movingVelocityDelta}`)

    velocity.x += movingVelocityDelta.x
    velocity.y += movingVelocityDelta.y
  }

  private recalculatePositionsAndResolveInterpenetration(collision: Collision, timeStepSecs: number): void {
    const object = collision.pair.first
    const otherObject = collision.pair.second

    object.calculateNextPosition(timeStepSecs)
    const moveMagnitude = Math.min(collision.contact.depth, 0.1)

    if (!otherObject) {
      object.applyNormalMoveToNextPosition(-moveMagnitude, collision.contact.angle)

      if (object instanceof VehicleCalculations || object instanceof WheelCalculations) {
        BodyUtils.applyNormalMoveToPosition(object.model.state, -moveMagnitude, collision.contact.angle)
      }

      if (this.logging) console.log(`First object normal move = ${-moveMagnitude}`)
    } else {
      otherObject.calculateNextPosition(timeStepSecs)
      const mass = object.getMass()
      const otherMass = otherObject.getMass()
      const normalMovePerMass = moveMagnitude / (mass + otherMass)
      const normalMove = normalMovePerMass * otherMass
      const otherNormalMove = normalMovePerMass * mass

      object.applyNormalMoveToNextPosition(-normalMove, collision.contact.angle)
      otherObject.applyNormalMoveToNextPosition(otherNormalMove, collision.contact.angle)

      if (object instanceof VehicleCalculations || object instanceof WheelCalculations) {
        BodyUtils.applyNormalMoveToPosition(object.model.state, -normalMove, collision.contact.angle)
      }

      if (otherObject instanceof VehicleCalculations || otherObject instanceof WheelCalculations) {
        BodyUtils.applyNormalMoveToPosition(otherObject.model.state, otherNormalMove, collision.contact.angle)
      }

      if (this.logging) {
        console.log(`First object normal move = ${-normalMove}`)
        console.log(`Second object normal move = ${otherNormalMove}`)
      }
    }
  }

  private multiplyBodyVelocity(velocity: BodyVelocity, multiplier: number): void {
    velocity.x *= multiplier
    velocity.y *= multiplier
    velocity.angle *= multiplier
  }

  private multiplyVelocity(velocity: Velocity, multiplier: number): void {
    velocity.x *= multiplier
    velocity.y *= multiplier
  }

  private getFrictionCoefficient(calculations: Calculations): number {
    if (calculations instanceof VehicleCalculations) {
      return 25.0
    }
    return 0.1
  }
}
