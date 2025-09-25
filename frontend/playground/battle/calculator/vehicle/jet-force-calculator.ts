import type {ForceCalculator} from "~/playground/battle/calculator/force-calculator";
import type {VehicleCalculations, WheelCalculations} from "~/playground/data/calculations";
import type {BattleModel, VehicleModel} from "~/playground/data/model";
import {BodyForce} from "~/playground/battle/calculator/body-force";
import {JetType, MovingDirection, zeroVector} from "~/playground/data/common";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {VehicleUtils} from "~/playground/utils/vehicle-utils";

export class JetForceCalculator implements ForceCalculator<VehicleCalculations> {
  static readonly FORCE_DESCRIPTION = 'Jet'
  static readonly HORIZONTAL_JET_ANGLE = Math.PI / 16

  calculate(calculations: VehicleCalculations, battleModel: BattleModel): BodyForce[] {
    const forces: BodyForce[] = []
    const vehicleModel = calculations.model
    const jetSpecs = vehicleModel.config.jet

    if (!jetSpecs) {
      return forces
    }

    const jetState = vehicleModel.state.jetState
    if (!jetState.active || jetState.volume <= 0) {
      return forces
    }

    const acceleration = jetSpecs.acceleration * calculations.model.preCalc.mass
    const direction = vehicleModel.state.movingDirection!
    const angle = vehicleModel.state.position.angle

    if (VehicleUtils.isTurnedOver(vehicleModel)) {
      this.addTurning(forces, calculations, acceleration, angle)
    } else if (jetSpecs.type === JetType.VERTICAL) {
      this.addVertical(forces, calculations.leftWheel, vehicleModel, acceleration / 2, angle, direction)
      this.addVertical(forces, calculations.rightWheel, vehicleModel, acceleration / 2, angle, direction)
    } else if (jetSpecs.type === JetType.HORIZONTAL) {
      this.addHorizontal(forces, acceleration, angle, direction)
    }

    return forces
  }

  private addVertical(
      forces: BodyForce[],
      wheelCalculations: WheelCalculations,
      vehicleModel: VehicleModel,
      acceleration: number,
      angle: number,
      direction: MovingDirection | null
  ): void {
    const angleCoefficient = 1 + wheelCalculations.sign * Math.sin(angle)
    const force = zeroVector()

    if (!direction) {
      force.x = 0.0
      force.y = acceleration * angleCoefficient
    } else if (direction === MovingDirection.RIGHT) {
      force.x = acceleration / Math.sqrt(2)
      force.y = (acceleration * angleCoefficient) / Math.sqrt(2)
    } else if (direction === MovingDirection.LEFT) {
      force.x = -acceleration / Math.sqrt(2)
      force.y = (acceleration * angleCoefficient) / Math.sqrt(2)
    }

    forces.push(
        BodyForce.of(
            force,
            wheelCalculations.position!,
            vehicleModel.state.position,
            JetForceCalculator.FORCE_DESCRIPTION
        )
    )
  }

  private addHorizontal(
      forces: BodyForce[],
      acceleration: number,
      angle: number,
      direction: MovingDirection | null
  ): void {
    if (direction === MovingDirection.RIGHT) {
      const force = {
        x: acceleration * Math.cos(angle + JetForceCalculator.HORIZONTAL_JET_ANGLE),
        y: acceleration * Math.sin(angle + JetForceCalculator.HORIZONTAL_JET_ANGLE)
      }
      forces.push(BodyForce.atCOM(force, JetForceCalculator.FORCE_DESCRIPTION))
    } else if (direction === MovingDirection.LEFT) {
      const force = {
        x: acceleration * Math.cos(angle - JetForceCalculator.HORIZONTAL_JET_ANGLE + Math.PI),
        y: acceleration * Math.sin(angle - JetForceCalculator.HORIZONTAL_JET_ANGLE + Math.PI)
      }
      forces.push(BodyForce.atCOM(force, JetForceCalculator.FORCE_DESCRIPTION))
    }
  }

  private addTurning(forces: BodyForce[], calculations: VehicleCalculations,
                     acceleration: number, angle: number) {
    const maxRadius = calculations.model.preCalc.maxRadius
    const comPosition = calculations.model.state.position
    const position = BattleUtils.shiftedPosition(comPosition, -maxRadius * Math.sign(angle), 0.0)
    forces.push(BodyForce.of({
      x: 0,
      y: acceleration * 1.5
    }, position, comPosition, JetForceCalculator.FORCE_DESCRIPTION))
    forces.push(BodyForce.atCOM({x: 0, y: acceleration * 0.5}, JetForceCalculator.FORCE_DESCRIPTION))
  }
}
