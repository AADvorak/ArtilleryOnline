import type {ShellModel} from "@/playground/data/model";
import type {RoomSpecs} from "~/playground/data/specs";

export const ShellProcessor = {
  processStep(shellModel: ShellModel, timeStepSecs: number, roomSpecs: RoomSpecs) {
    if (shellModel.state.stuck) {
      return
    }
    const prevPosition = shellModel.state.position
    let velocity = shellModel.state.velocity
    const nextPosition = {
      x: prevPosition.x + velocity.x * timeStepSecs,
      y: prevPosition.y + velocity.y * timeStepSecs
    }
    if (nextPosition.x >= roomSpecs.rightTop.x || nextPosition.x <= roomSpecs.leftBottom.x) {
      velocity.x = - velocity.x
    }
    velocity.y -= 2.25 * roomSpecs.gravityAcceleration * timeStepSecs
    shellModel.state.position = nextPosition
  }
}
