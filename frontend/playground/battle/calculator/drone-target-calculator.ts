import type {DroneCalculations} from "~/playground/data/calculations";
import type {BattleModel} from "~/playground/data/model";
import type {Position} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {TargetCalculator} from "~/playground/battle/calculator/target-calculator";

export const DroneTargetCalculator = {
  calculate(drone: DroneCalculations, battleModel: BattleModel) {
    if (drone.model.state.destroyed) return

    const ammo = Object.values(drone.model.state.ammo)[0]
    let targets: Position[] = []

    if (ammo && ammo > 0) {
      targets = TargetCalculator.calculatePositions(drone.model.vehicleId, battleModel)
    } else {
      targets = Object.values(battleModel.vehicles)
          .filter(vehicle => vehicle.id === drone.model.vehicleId)
          .map(vehicle => ({
            x: vehicle.state.position.x,
            y: vehicle.state.position.y
          }))
    }

    if (targets.length === 0) return

    const dronePosition = drone.model.state.position
    const xDiffMap: { [key: number]: Position } = {}

    targets.forEach(position => {
      const xDiff = position.x - dronePosition.x
      xDiffMap[xDiff] = position
    })

    let minXDiff = Math.min(...Object.keys(xDiffMap).map(Number))

    const targetPosition = xDiffMap[minXDiff]!
    const gunAngle = drone.model.state.gunAngle + drone.model.state.position.angle
    const angleDiff = BattleUtils.calculateAngleDiff(gunAngle, VectorUtils.angleFromTo(dronePosition, targetPosition))

    drone.target = {
      xDiff: minXDiff,
      angleDiff: angleDiff
    }
  }
}
