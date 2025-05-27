import type {ForceCalculator} from "~/playground/battle/calculator/force-calculator";
import type {BodyCalculations} from "~/playground/data/calculations";
import type {BattleModel} from "~/playground/data/model";
import {type BodyAcceleration, cloneVector, MovingDirection, zeroBodyVector} from "~/playground/data/common";
import {BodyForce} from "~/playground/battle/calculator/body-force";
import {VectorUtils} from "~/playground/utils/vector-utils";

export class BodyAccelerationCalculator {
  private static readonly COLLIDER_PUSHING = 5.0
  private static readonly COLLIDER_ROTATING = 2.0
  private static readonly FORCES_TO_EXTRACT_MOVING_FROM_ROTATING = [
    // todo Jet
  ]

  private forceCalculators: ForceCalculator[]

  constructor(forceCalculators: ForceCalculator[]) {
    this.forceCalculators = forceCalculators
  }

  calculate(calculations: BodyCalculations, battleModel: BattleModel): BodyAcceleration {
    const forces: BodyForce[] = []
    const accelerations: BodyAcceleration[] = []

    for (const forceCalculator of this.forceCalculators) {
      forces.push(...forceCalculator.calculate(calculations, battleModel))
    }

    this.extractMovingFromRotating(forces)

    for (const force of forces) {
      accelerations.push(this.toAcceleration(force, calculations))
    }

    this.addAirFrictionAcceleration(accelerations, calculations, battleModel.room.specs.airFrictionCoefficient)

    this.addColliderAcceleration(accelerations, calculations)

    return VectorUtils.sumOfBodyArr(accelerations)
  }

  private toAcceleration(force: BodyForce, calculations: BodyCalculations): BodyAcceleration {
    const mass = calculations.model.preCalc.mass
    const acceleration: BodyAcceleration = zeroBodyVector()
    if (force.moving) {
      acceleration.x = force.moving.x / mass
      acceleration.y = force.moving.y / mass
    }
    if (force.rotating && force.radiusVector) {
      const momentOfInertia = calculations.model.preCalc.momentOfInertia
      acceleration.angle = force.torque() / momentOfInertia
    }
    return acceleration
  }

  private extractMovingFromRotating(forces: BodyForce[]): void {
    const extractedForces: BodyForce[] = []
    for (let i = 0; i < forces.length; i++) {
      const force1 = forces[i]
      for (let j = i + 1; j < forces.length; j++) {
        const force2 = forces[j]
        if (this.canExtractMovingFromRotating(force1, force2)) {
          const extractedForce = this.extractMovingFromRotatingPair(force1, force2)
          if (extractedForce !== null) {
            extractedForces.push(extractedForce)
          }
        }
      }
    }
    forces.push(...extractedForces)
  }

  private canExtractMovingFromRotating(force1: BodyForce, force2: BodyForce): boolean {
    return (
        force1.rotating &&
        force2.rotating &&
        BodyAccelerationCalculator.FORCES_TO_EXTRACT_MOVING_FROM_ROTATING.includes(force1.description) &&
        BodyAccelerationCalculator.FORCES_TO_EXTRACT_MOVING_FROM_ROTATING.includes(force2.description)
    )
  }

  private extractMovingFromRotatingPair(force1: BodyForce, force2: BodyForce): BodyForce | null {
    const torque1 = force1.torque()
    const torque2 = force2.torque()
    if (torque1 * torque2 >= 0) {
      return null
    }
    const absTorque1 = Math.abs(torque1)
    const absTorque2 = Math.abs(torque2)
    if (absTorque1 > absTorque2) {
      return this.extractMovingFromRotatingDetailed(force1, force2, absTorque1, absTorque2)
    } else {
      return this.extractMovingFromRotatingDetailed(force2, force1, absTorque2, absTorque1)
    }
  }

  private extractMovingFromRotatingDetailed(
      greater: BodyForce,
      lower: BodyForce,
      greaterTorque: number,
      lowerTorque: number
  ): BodyForce {
    const diff = greaterTorque - lowerTorque
    const rotatingPart = diff / greaterTorque
    const movingPart = (greaterTorque - diff) / greaterTorque

    const newRotating = VectorUtils.multiply(greater.rotating!, rotatingPart)
    const movingFromGreater = VectorUtils.multiply(greater.rotating!, movingPart)
    const movingFromLower = cloneVector(lower.rotating!)

    greater.rotating = newRotating
    lower.rotating = null

    return BodyForce.atCOM(
        VectorUtils.sumOf(movingFromGreater, movingFromLower),
        `Extracted from ${greater.description} and ${lower.description}`
    )
  }

  private addColliderAcceleration(accelerations: BodyAcceleration[], calculations: BodyCalculations): void {
    const pushingDirection = calculations.model.state.pushingDirection
    const rotatingDirection = calculations.model.state.rotatingDirection
    const acceleration = zeroBodyVector()

    if (pushingDirection) {
      switch (pushingDirection) {
        case MovingDirection.UP:
          acceleration.y = BodyAccelerationCalculator.COLLIDER_PUSHING
          break
        case MovingDirection.DOWN:
          acceleration.y = -BodyAccelerationCalculator.COLLIDER_PUSHING
          break
        case MovingDirection.LEFT:
          acceleration.x = -BodyAccelerationCalculator.COLLIDER_PUSHING
          break
        case MovingDirection.RIGHT:
          acceleration.x = BodyAccelerationCalculator.COLLIDER_PUSHING
          break
      }
    }

    if (rotatingDirection !== null) {
      switch (rotatingDirection) {
        case MovingDirection.RIGHT:
          acceleration.angle = -BodyAccelerationCalculator.COLLIDER_ROTATING
          break
        case MovingDirection.LEFT:
          acceleration.angle = BodyAccelerationCalculator.COLLIDER_ROTATING
          break
      }
    }

    accelerations.push(acceleration)
  }

  addAirFrictionAcceleration(
      accelerations: BodyAcceleration[],
      calculations: BodyCalculations,
      frictionCoefficient: number
  ): void {
    const model = calculations.model
    const state = model.state
    const preCalc = model.preCalc
    const velocity = state.velocity

    const maxRadius = preCalc.maxRadius
    const mass = preCalc.mass
    const momentOfInertia = preCalc.momentOfInertia

    const movingCoefficient = (2 * maxRadius * frictionCoefficient) / mass
    const rotatingCoefficient = (2 * Math.PI * maxRadius * maxRadius * frictionCoefficient) / momentOfInertia

    accelerations.push(
        {
          x: -velocity.x * Math.abs(velocity.x) * movingCoefficient,
          y: -velocity.y * Math.abs(velocity.y) * movingCoefficient,
          angle: -velocity.angle * Math.abs(velocity.angle) * rotatingCoefficient
        }
    )
  }
}
