import type {MissileModel} from "~/playground/data/model";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {TargetCalculator} from "~/playground/battle/calculator/target-calculator";
import type {MissileSpecs} from "~/playground/data/specs";
import type {BattleCalculations} from "~/playground/data/calculations";

const PRECISION_THRESHOLD = 6.0
const CLOSE_EDGE_DISTANCE = 5.0
const CLOSE_EDGE_ANGLE = -Math.PI / 4
const FAR_EDGE_DISTANCE = 10.0
const FAR_EDGE_ANGLE = Math.PI / 5

const K = (FAR_EDGE_ANGLE - CLOSE_EDGE_ANGLE) / (FAR_EDGE_DISTANCE - CLOSE_EDGE_DISTANCE)
const B = CLOSE_EDGE_ANGLE - CLOSE_EDGE_DISTANCE * K

class TargetData {
  constructor(
      public angle: number,
      public distance: number,
      public xDistance: number
  ) {}
}

export const CorrectingAccelerationCalculator = {

  calculate(missileModel: MissileModel, battle: BattleCalculations): number {
    const state = missileModel.state
    const specs = missileModel.specs
    const velocityMagnitude = VectorUtils.getMagnitude(state.velocity)
    const correctingVelocity = velocityMagnitude - specs.minCorrectingVelocity

    const missilePosition = state.position

    if (correctingVelocity <= 0) {
      return this.getAcceleration(
          missilePosition.angle,
          Math.PI / 2,
          specs,
          velocityMagnitude
      )
    }

    const targets = TargetCalculator.calculatePositions(missileModel.vehicleId, battle)
    if (!targets.length) {
      return 0.0
    }

    const targetDataSet = new Set<TargetData>()
    for (const position of targets) {
      const angle = VectorUtils.angleFromTo(missilePosition, position)
      const distance = BattleUtils.distance(missilePosition, position)
      const xDistance = position.x - missilePosition.x
      targetDataSet.add(new TargetData(angle, distance, xDistance))
    }

    let closestTarget = null as TargetData | null
    for (const td of targetDataSet) {
      if (!closestTarget) {
        closestTarget = td
      } else if (Math.abs(td.xDistance) < Math.abs(closestTarget.xDistance)) {
        closestTarget = td
      }
    }

    if (!closestTarget) {
      return 0.0
    }

    const absXDistance = Math.abs(closestTarget.xDistance)

    if (absXDistance > FAR_EDGE_DISTANCE) {
      const tagetAngle = closestTarget.xDistance > 0 ? FAR_EDGE_ANGLE : Math.PI - FAR_EDGE_ANGLE
      return CorrectingAccelerationCalculator.getAcceleration(
          missilePosition.angle,
          tagetAngle,
          specs,
          velocityMagnitude
      )
    }

    if (absXDistance > CLOSE_EDGE_DISTANCE) {
      const angle = K * absXDistance + B
      const tagetAngle = closestTarget.xDistance > 0 ? angle : Math.PI - angle
      return CorrectingAccelerationCalculator.getAcceleration(
          missilePosition.angle,
          tagetAngle,
          specs,
          velocityMagnitude
      )
    }

    const angleDiff = BattleUtils.calculateAngleDiff(missilePosition.angle, closestTarget.angle)
    if (Math.abs(angleDiff) < specs.anglePrecision
        * CorrectingAccelerationCalculator.getAnglePrecisionCoefficient(closestTarget.distance)) {
      return 0.0
    }

    return Math.sign(angleDiff) * correctingVelocity * specs.correctingAccelerationCoefficient
  },

  getAcceleration(
      missileAngle: number,
      targetAngle: number,
      specs: MissileSpecs,
      velocityMagnitude: number
  ): number {
    return Math.sign(BattleUtils.calculateAngleDiff(missileAngle, targetAngle)) * velocityMagnitude
        * specs.correctingAccelerationCoefficient / specs.minCorrectingVelocity
  },

  getAnglePrecisionCoefficient(distance: number): number {
    if (distance < PRECISION_THRESHOLD) {
      return distance / PRECISION_THRESHOLD
    }
    return 1.0
  }
}
