import type {BodyState} from "~/playground/data/state";
import {type BodyPosition, MovingDirection, type Position, type Velocity} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";
import type {BodyCalculations, VehicleCalculations, WheelCalculations} from "~/playground/data/calculations";
import type {BodyModel, VehicleModel} from "~/playground/data/model";

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

  getWheelVelocityAt(wheelCalculations: WheelCalculations, vehicleModel: VehicleModel, position: Position): Velocity {
    let angleVelocity = 0.0
    const movingDirection = vehicleModel.state.movingDirection
    var angleVelocitySpecs = vehicleModel.specs.wheelAngleVelocity
    if (MovingDirection.RIGHT === movingDirection) {
      angleVelocity = - angleVelocitySpecs
    }
    if (MovingDirection.LEFT === movingDirection) {
      angleVelocity = angleVelocitySpecs
    }
    const velocityAt = {
        x: wheelCalculations.velocity!.x,
        y: wheelCalculations.velocity!.y
    }
    if (angleVelocity != 0.0) {
      const angle = VectorUtils.angleFromTo(wheelCalculations.position!, position)
      const distance = BattleUtils.distance(wheelCalculations.position!, position)
      velocityAt.x -= angleVelocity * distance * Math.sin(angle)
      velocityAt.y += angleVelocity * distance * Math.cos(angle)
    }
    return velocityAt
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
}
