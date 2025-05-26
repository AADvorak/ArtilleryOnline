import type {BodyState} from "~/playground/data/state";
import type {Position, Velocity} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";

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
  }
}
