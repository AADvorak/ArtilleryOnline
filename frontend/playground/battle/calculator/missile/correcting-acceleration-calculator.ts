import type {BattleModel, MissileModel} from "~/playground/data/model";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";

export const CorrectingAccelerationCalculator = {
  calculate(missileModel: MissileModel, battleModel: BattleModel): number {
    const missileState = missileModel.state
    const velocityMagnitude = VectorUtils.getMagnitude(missileState.velocity)
    const correctingVelocity = velocityMagnitude - missileModel.specs.minCorrectingVelocity
    const missilePosition = missileState.position

    if (correctingVelocity <= 0) {
      const verticalAngleDiff = BattleUtils.calculateAngleDiff(missilePosition.angle, Math.PI / 2)
      if (Math.abs(verticalAngleDiff) < missileModel.specs.anglePrecision) {
        return 0.0
      }
      return Math.sign(verticalAngleDiff) * velocityMagnitude
          * missileModel.specs.correctingAccelerationCoefficient / missileModel.specs.minCorrectingVelocity
    }

    const targets = Object.values(battleModel.vehicles).filter(vehicleModel =>
        vehicleModel.id !== missileModel.vehicleId
    )

    if (targets.length === 0) {
      return 0.0
    }

    const angleDiffs = targets.map(vehicleModel => {
      return BattleUtils.calculateAngleDiff(
          missilePosition.angle,
          VectorUtils.angleFromTo(missilePosition, vehicleModel.state.position)
      )
    })

    const minAngleDiff = angleDiffs.reduce((minDiff, angleDiff) =>
            Math.abs(angleDiff) < Math.abs(minDiff) ? angleDiff : minDiff,
        angleDiffs[0]
    )

    if (Math.abs(minAngleDiff) < missileModel.specs.anglePrecision) {
      return 0.0
    }

    return Math.sign(minAngleDiff) * correctingVelocity * missileModel.specs.correctingAccelerationCoefficient
  }
}
