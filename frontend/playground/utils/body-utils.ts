import type {BodyState} from "~/playground/data/state";
import {type BodyPosition, type Position, type Velocity} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";
import type {BodyCalculations, VehicleCalculations} from "~/playground/data/calculations";
import type {BodyModel} from "~/playground/data/model";
import {VectorProjections} from "~/playground/data/geometry";

export const BodyUtils = {
  getVelocityAt(bodyState: BodyState, position: Position): Velocity {
    const rotatingVelocity = this.getRotatingVelocityAt(bodyState, position);
    return {
      x: bodyState.velocity.x + rotatingVelocity.x,
      y: bodyState.velocity.y + rotatingVelocity.y
    }
  },

  getRotatingVelocityAt(bodyState: BodyState, position: Position): Velocity {
    const bodyVelocity = bodyState.velocity
    const comPosition = bodyState.position
    const angle = VectorUtils.angleFromTo(comPosition, position)
    const distance = BattleUtils.distance(comPosition, position)
    return {
      x: -bodyVelocity.angle * distance * Math.sin(angle),
      y: bodyVelocity.angle * distance * Math.cos(angle)
    }
  },

  getAllGroundContacts(calculations: BodyCalculations) {
    const allContacts = []
    if (calculations.groundContacts) {
      calculations.groundContacts.forEach(contact => allContacts.push(contact))
    }
    const vehicleCalculations = calculations as VehicleCalculations
    if (vehicleCalculations.leftWheel?.groundContact) {
      allContacts.push(vehicleCalculations.leftWheel.groundContact)
    }
    if (vehicleCalculations.rightWheel?.groundContact) {
      allContacts.push(vehicleCalculations.rightWheel.groundContact)
    }
    return allContacts
  },

  getGeometryPosition(bodyModel: BodyModel) {
    const position = bodyModel.state.position
    const comShift = bodyModel.preCalc.centerOfMassShift
    return BattleUtils.shiftedPosition(position, - comShift.distance, position.angle + comShift.angle)
  },

  getGeometryBodyPosition(bodyModel: BodyModel): BodyPosition {
    const position = bodyModel.state.position
    const gPosition = this.getGeometryPosition(bodyModel)
    return {
      x: gPosition.x,
      y: gPosition.y,
      angle: position.angle
    }
  },

  recalculateBodyPosition(bodyModel: BodyModel, timeStepSecs: number) {
    const velocity = bodyModel.state.velocity
    const position = bodyModel.state.position
    position.x += velocity.x * timeStepSecs
    position.y += velocity.y * timeStepSecs
    position.angle += velocity.angle * timeStepSecs
  },

  applyNormalMoveToPosition(bodyState: BodyState, normalMove: number, angle: number) {
    const move = new VectorProjections(angle, normalMove, 0).recoverPosition()
    bodyState.position.x += move.x
    bodyState.position.y += move.y
  },

  getBackupPosition(bodyModel: BodyModel): BodyPosition {
    const position = bodyModel.state.position
    return {
      x: position.x,
      y: position.y,
      angle: position.angle
    }
  },
}
