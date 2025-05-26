import type {ForceCalculator} from "~/playground/battle/calculator/force-calculator";
import {type VehicleCalculations, type WheelCalculations, WheelGroundState} from "~/playground/data/calculations";
import type {BattleModel} from "~/playground/data/model";
import {BodyForce} from "~/playground/battle/calculator/body-force";
import {VectorUtils} from "~/playground/utils/vector-utils";
import type {Position} from "~/playground/data/common";
import {BodyUtils} from "~/playground/utils/body-utils";

export class GroundFrictionForceCalculator implements ForceCalculator {
  private static readonly FORCE_DESCRIPTION = 'Ground Friction'

  calculate(calculations: VehicleCalculations, battleModel: BattleModel): BodyForce[] {
    const groundFrictionCoefficient = battleModel.room.specs.groundFrictionCoefficient
    const gravityAcceleration = battleModel.room.specs.gravityAcceleration
    const comPosition = calculations.model.state.position
    const forces: BodyForce[] = []

    this.addWheelFriction(forces, calculations.rightWheel, comPosition, groundFrictionCoefficient)
    this.addWheelFriction(forces, calculations.leftWheel, comPosition, groundFrictionCoefficient)
    this.addHullFriction(forces, calculations, groundFrictionCoefficient, gravityAcceleration)

    return forces
  }

  private addWheelFriction(
      forces: BodyForce[],
      wheelCalculations: WheelCalculations,
      comPosition: Position,
      groundFrictionCoefficient: number
  ): void {
    if (wheelCalculations.groundState === WheelGroundState.FULL_OVER_GROUND) {
      return
    }

    const depth = wheelCalculations.groundContact!.depth
    const position = wheelCalculations.groundContact!.position
    const velocity = VectorUtils.projectionOfOnto(
        wheelCalculations.velocity!,
        VectorUtils.tangential(wheelCalculations.groundContact!.angle)
    )

    const force = {
      x: -velocity.x * depth * groundFrictionCoefficient,
      y: -velocity.y * depth * groundFrictionCoefficient
    }

    forces.push(BodyForce.of(
        force,
        position,
        comPosition,
        GroundFrictionForceCalculator.FORCE_DESCRIPTION + ' Wheel'
    ))
  }

  private addHullFriction(
      forces: BodyForce[],
      calculations: VehicleCalculations,
      groundFrictionCoefficient: number,
      gravityAcceleration: number
  ): void {
    const groundContacts = calculations.groundContacts
    if (!groundContacts) {
      return
    }

    const forceMultiplier = 0.1 * groundFrictionCoefficient * gravityAcceleration
        * calculations.model.preCalc.mass

    groundContacts.forEach(contact => {
      const tangential = VectorUtils.tangential(contact.angle)
      const movingVelocity = VectorUtils.projectionOfOnto(calculations.model.state.velocity, tangential)
      const rotatingVelocity = VectorUtils.projectionOfOnto(
          BodyUtils.getRotatingVelocityAt(calculations.model.state, contact.position), tangential
      )
      const movingVelocityMagnitude = VectorUtils.getMagnitude(movingVelocity)
      const rotatingVelocityMagnitude = VectorUtils.getMagnitude(rotatingVelocity)
      const contactForceMultiplier = forceMultiplier * Math.cos(contact.angle)

      if (movingVelocityMagnitude > rotatingVelocityMagnitude || rotatingVelocityMagnitude < 0.05) {
        const velocity = BodyUtils.getVelocityAt(calculations.model.state, contact.position)
        const movingForce = {
          x: -velocity.x * contactForceMultiplier,
          y: -velocity.y * contactForceMultiplier
        }
        forces.push(BodyForce.of(
            movingForce,
            contact.position,
            calculations.model.state.position,
            GroundFrictionForceCalculator.FORCE_DESCRIPTION + ' Hull'
        ))
      } else {
        const rotatingForce = {
          x: -rotatingVelocity.x * contactForceMultiplier,
          y: -rotatingVelocity.y * contactForceMultiplier
        }
        forces.push(BodyForce.atCOM(rotatingForce, GroundFrictionForceCalculator.FORCE_DESCRIPTION + ' Hull'))
      }
    })
  }
}
