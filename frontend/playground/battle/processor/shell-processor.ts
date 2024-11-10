import type {ShellModel} from "@/playground/data/model";
import type {RoomSpecs} from "~/playground/data/specs";

export const ShellProcessor = {
  processStep(shellModel: ShellModel, timeStepSecs: number, roomSpecs: RoomSpecs) {
    const prevPosition = shellModel.state.position
    let velocity = shellModel.state.velocity
    let angle = shellModel.state.angle
    let velocityX = velocity * Math.cos(angle)
    let velocityY = velocity * Math.sin(angle)
    const nextPosition = {
      x: prevPosition.x + velocityX * timeStepSecs,
      y: prevPosition.y + velocityY * timeStepSecs
    }
    if (nextPosition.x >= roomSpecs.rightTop.x || nextPosition.x <= roomSpecs.leftBottom.x) {
      velocityX = - velocityX
    }
    velocityY -= roomSpecs.gravityAcceleration * timeStepSecs
    velocity = Math.sqrt(Math.pow(velocityX, 2.0) + Math.pow(velocityY, 2.0))
    angle = Math.atan(velocityY / velocityX) + (velocityX < 0 ? Math.PI : 0.0)
    shellModel.state.position = nextPosition
    shellModel.state.velocity = velocity
    shellModel.state.angle = angle
  }
}
