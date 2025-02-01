import type {BattleModel, MissileModel} from "~/playground/data/model";
import {VectorUtils} from "~/playground/utils/vector-utils";

export const CorrectingAccelerationCalculator = {
  calculate(missileModel: MissileModel, battleModel: BattleModel): number {
    const missileState = missileModel.state
    const correctingVelocity = VectorUtils.getMagnitude(missileState.velocity)
        - missileModel.specs.minCorrectingVelocity

    if (correctingVelocity <= 0) {
      return 0.0
    }

    const targets = Object.values(battleModel.vehicles).filter(vehicleModel =>
        vehicleModel.id !== missileModel.vehicleId
    )

    if (targets.length === 0) {
      return 0.0
    }

    const angleDiffs = targets.map(vehicleModel => {
      const missilePosition = missileState.position
      return this.calculateAngleDiff(
          missilePosition.angle,
          VectorUtils.angleFromTo(missilePosition, vehicleModel.state.position)
      )
    })

    const minAngleDiff = angleDiffs.reduce((minDiff, angleDiff) =>
            Math.abs(angleDiff) < Math.abs(minDiff) ? angleDiff : minDiff,
        angleDiffs[0]
    )

    return Math.sign(minAngleDiff) * correctingVelocity * missileModel.specs.correctingAccelerationCoefficient
  },

  calculateAngleDiff(missileAngle: number, targetAngle: number): number {
    let diff = targetAngle - missileAngle

    if (Math.abs(diff) > Math.PI) {
      if (diff > 0) {
        return 2 * Math.PI - diff
      } else {
        return 2 * Math.PI + diff
      }
    } else {
      return diff
    }
  }
}
