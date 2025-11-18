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

    const magnitude = jetSpecs.acceleration * calculations.model.preCalc.mass
    const direction = vehicleModel.state.movingDirection!
    const angle = vehicleModel.state.position.angle

    if (VehicleUtils.isAboutToTurnOver(vehicleModel)) {
      this.addTurning(forces, calculations, magnitude, angle)
    } else if (jetSpecs.type === JetType.VERTICAL) {
      this.addVerticalUp(forces, calculations.leftWheel, vehicleModel, magnitude / 2, angle, direction)
      this.addVerticalUp(forces, calculations.rightWheel, vehicleModel, magnitude / 2, angle, direction)
      this.addVerticalSide(forces, magnitude / 2, direction)
    } else if (jetSpecs.type === JetType.HORIZONTAL) {
      direction === MovingDirection.RIGHT && this.addHorizontalForWheel(forces, calculations.rightWheel,
          vehicleModel, magnitude, direction)
      direction === MovingDirection.LEFT && this.addHorizontalForWheel(forces, calculations.leftWheel,
          vehicleModel, magnitude, direction)
      if (forces.length === 0) {
        this.addHorizontal(forces, magnitude, angle, direction)
      }
    }

    return forces
  }

  private addVerticalUp(
      forces: BodyForce[],
      wheelCalculations: WheelCalculations,
      vehicleModel: VehicleModel,
      magnitude: number,
      angle: number,
      direction: MovingDirection | null
  ): void {
    const angleCoefficient = 1 + wheelCalculations.sign * Math.sin(angle)
    const movingCoefficient = direction ? 0.5 : 1.0
    const force = zeroVector()
    force.y = magnitude * angleCoefficient * movingCoefficient

    forces.push(
        BodyForce.of(
            force,
            wheelCalculations.position!,
            vehicleModel.state.position,
            JetForceCalculator.FORCE_DESCRIPTION
        )
    )
  }

  private addVerticalSide(
      forces: BodyForce[],
      magnitude: number,
      direction: MovingDirection | null
  ): void {
    if (direction === MovingDirection.RIGHT) {
      const force = zeroVector()
      force.x = magnitude
      forces.push(
          BodyForce.atCOM(
              force,
              JetForceCalculator.FORCE_DESCRIPTION
          )
      )
    }
    if (direction === MovingDirection.LEFT) {
      const force = zeroVector()
      force.x = -magnitude
      forces.push(
          BodyForce.atCOM(
              force,
              JetForceCalculator.FORCE_DESCRIPTION
          )
      )
    }
  }

  private addHorizontal(
      forces: BodyForce[],
      magnitude: number,
      angle: number,
      direction: MovingDirection | null
  ): void {
    const additionalAngle = this.getHorizontalJetAdditionalAngle(angle)
    if (direction === MovingDirection.RIGHT) {
      const force = {
        x: magnitude * Math.cos(angle + additionalAngle),
        y: magnitude * Math.sin(angle + additionalAngle)
      }
      forces.push(BodyForce.atCOM(force, JetForceCalculator.FORCE_DESCRIPTION))
    } else if (direction === MovingDirection.LEFT) {
      const force = {
        x: magnitude * Math.cos(angle - additionalAngle + Math.PI),
        y: magnitude * Math.sin(angle - additionalAngle + Math.PI)
      }
      forces.push(BodyForce.atCOM(force, JetForceCalculator.FORCE_DESCRIPTION))
    }
  }

  private addHorizontalForWheel(
      forces: BodyForce[],
      calculations: WheelCalculations,
      vehicleModel: VehicleModel,
      magnitude: number,
      direction: MovingDirection | null
  ): void {
    const contact = calculations.groundContact
    if (!contact) {
      return
    }
    const force = zeroVector()

    if (direction === MovingDirection.RIGHT) {
      force.x = magnitude * Math.cos(contact.angle + JetForceCalculator.HORIZONTAL_JET_ANGLE)
      force.y = magnitude * Math.sin(contact.angle + JetForceCalculator.HORIZONTAL_JET_ANGLE)
    } else if (direction === MovingDirection.LEFT) {
      force.x = -magnitude * Math.cos(contact.angle - JetForceCalculator.HORIZONTAL_JET_ANGLE)
      force.y = -magnitude * Math.sin(contact.angle - JetForceCalculator.HORIZONTAL_JET_ANGLE)
    }
    forces.push(
        BodyForce.of(force, calculations.position!, vehicleModel.state.position, JetForceCalculator.FORCE_DESCRIPTION)
    )
  }

  private getHorizontalJetAdditionalAngle(angle: number): number {
    const absAngle = Math.abs(angle)
    if (absAngle > Math.PI / 2) {
      return angle
    }
    const absAdditionalAngle = JetForceCalculator.HORIZONTAL_JET_ANGLE
        - 4 * JetForceCalculator.HORIZONTAL_JET_ANGLE * absAngle / Math.PI
    return absAngle < Math.PI / 4 ? absAdditionalAngle : -absAdditionalAngle
  }

  private addTurning(forces: BodyForce[], calculations: VehicleCalculations,
                     magnitude: number, angle: number) {
    const maxRadius = calculations.model.preCalc.maxRadius
    const comPosition = calculations.model.state.position
    const position = BattleUtils.shiftedPosition(comPosition, -maxRadius * Math.sign(angle), 0.0)
    forces.push(BodyForce.of({
      x: 0,
      y: magnitude * 1.5
    }, position, comPosition, JetForceCalculator.FORCE_DESCRIPTION))
    forces.push(BodyForce.atCOM({x: 0, y: magnitude * 0.5}, JetForceCalculator.FORCE_DESCRIPTION))
  }
}
