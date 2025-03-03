import type {BattleModel} from "~/playground/data/model";
import type {Position} from "~/playground/data/common";

export const TargetCalculator = {
  calculatePositions: function (vehicleId: number | undefined, battleModel: BattleModel): Position[] {
    let positions = Object.values(battleModel.vehicles)
        .filter(vehicle => !vehicleId || vehicle.id !== vehicleId)
        .map(vehicle => vehicle.state.position)
    if (positions.length > 0) {
      return positions
    }

    positions = Object.values(battleModel.drones)
        .filter(drone => !drone.vehicleId || drone.vehicleId !== vehicleId)
        .map(drone => drone.state.position)
    if (positions.length > 0) {
      return positions
    }

    return Object.values(battleModel.missiles)
        .filter(missile => !vehicleId || missile.vehicleId !== vehicleId)
        .map(missile => missile.state.position)
  }
}
