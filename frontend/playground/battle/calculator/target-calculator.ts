import type {Position} from "~/playground/data/common";
import type {BattleCalculations} from "~/playground/data/calculations";

export const TargetCalculator = {
  calculatePositions: function (vehicleId: number | undefined, battle: BattleCalculations): Position[] {
    let positions = Object.values(battle.model.vehicles)
        .filter(vehicle => !vehicleId || battle.allowedTarget(vehicle.id, vehicleId))
        .map(vehicle => vehicle.state.position)
    if (positions.length > 0) {
      return positions
    }

    positions = Object.values(battle.model.drones)
        .filter(drone => !drone.vehicleId || !vehicleId || battle.allowedTarget(drone.vehicleId, vehicleId))
        .map(drone => drone.state.position)
    if (positions.length > 0) {
      return positions
    }

    return Object.values(battle.model.missiles)
        .filter(missile => !vehicleId || battle.allowedTarget(missile.vehicleId, vehicleId))
        .map(missile => missile.state.position)
  }
}
