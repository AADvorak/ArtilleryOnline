import type {ForceCalculator} from "~/playground/battle/calculator/force-calculator";
import type {VehicleCalculations, WheelCalculations} from "~/playground/data/calculations";
import type {BattleModel, VehicleModel} from "~/playground/data/model";
import {BodyForce} from "~/playground/battle/calculator/body-force";
import {JetType, MovingDirection, zeroVector} from "~/playground/data/common";

export class JetForceCalculator implements ForceCalculator<VehicleCalculations> {
  static readonly FORCE_DESCRIPTION = 'Jet'

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
    const direction = vehicleModel.state.movingDirection
    const angle = vehicleModel.state.position.angle

    if (jetSpecs.type === JetType.VERTICAL) {
      JetForceCalculator.calculateVertical(forces, calculations.leftWheel, vehicleModel, acceleration / 2, angle, direction)
      JetForceCalculator.calculateVertical(forces, calculations.rightWheel, vehicleModel, acceleration / 2, angle, direction)
    }

    if (jetSpecs.type === JetType.HORIZONTAL) {
      this.addHorizontal(forces, acceleration, angle, direction)
    }

    return forces
  }

  private static calculateVertical(
      forces: BodyForce[],
      wheelCalculations: WheelCalculations,
      vehicleModel: VehicleModel,
      acceleration: number,
      angle: number,
      direction: MovingDirection | null
  ): void {
    const angleCoefficient = 1 + wheelCalculations.sign * Math.sin(angle)
    const force = zeroVector()

    if (direction === null) {
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
    const additionalAngle = Math.PI / 16

    if (direction === MovingDirection.RIGHT) {
      const force = {
        x: acceleration * Math.cos(angle + additionalAngle),
        y: acceleration * Math.sin(angle + additionalAngle)
      }
      forces.push(BodyForce.atCOM(force, JetForceCalculator.FORCE_DESCRIPTION))
    } else if (direction === MovingDirection.LEFT) {
      const force = {
        x: acceleration * Math.cos(angle - additionalAngle + Math.PI),
        y: acceleration * Math.sin(angle - additionalAngle + Math.PI)
      }
      forces.push(BodyForce.atCOM(force, JetForceCalculator.FORCE_DESCRIPTION))
    }
  }
}
