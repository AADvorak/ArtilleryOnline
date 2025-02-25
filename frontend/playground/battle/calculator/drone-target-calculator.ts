import type {DroneCalculations} from "~/playground/data/calculations";
import type {BattleModel} from "~/playground/data/model";
import type {Position} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";

export const DroneTargetCalculator = {
  calculate(drone: DroneCalculations, battleModel: BattleModel) {
    if (drone.model.destroyed) return

    const ammo = Object.values(drone.model.state.ammo)[0]
    let targets = Object.values(battleModel.vehicles)

    if (ammo > 0) {
      targets = targets.filter(vehicle => vehicle.id !== drone.model.vehicleId)
    } else {
      targets = targets.filter(vehicle => vehicle.id === drone.model.vehicleId)
    }

    if (targets.length === 0) return

    const dronePosition = drone.model.state.position
    const xDiffMap: { [key: number]: Position } = {}

    targets.forEach(vehicle => {
      const xDiff = vehicle.state.position.x - dronePosition.x
      xDiffMap[xDiff] = vehicle.state.position
    })

    let minXDiff = Math.min(...Object.keys(xDiffMap).map(Number))

    const targetPosition = xDiffMap[minXDiff]
    const gunAngle = drone.model.state.gunAngle + drone.model.state.position.angle
    const angleDiff = BattleUtils.calculateAngleDiff(gunAngle, VectorUtils.angleFromTo(dronePosition, targetPosition))

    drone.model.state.gunState.triggerPushed = Math.abs(angleDiff) < Math.PI / 32
        && BattleUtils.distance(dronePosition, targetPosition) < drone.model.specs.flyHeight

    drone.target = {
      xDiff: minXDiff,
      angleDiff: angleDiff
    }
  }
}
