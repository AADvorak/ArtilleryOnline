import type {ShellModel} from "@/data/model";

export const ShellProcessor = {
  processStep(shellModel: ShellModel, timeStepSecs: number, gravityAcceleration: number) {
    const prevPosition = shellModel.state.position
    let velocity = shellModel.state.velocity
    let angle = shellModel.state.angle
    let velocityX = velocity * Math.cos(angle)
    let velocityY = velocity * Math.sin(angle)
    const nextPosition = {
      x: prevPosition.x + velocityX * timeStepSecs,
      y: prevPosition.y + velocityY * timeStepSecs
    }
    velocityY -= gravityAcceleration * timeStepSecs
    velocity = Math.sqrt(Math.pow(velocityX, 2.0) + Math.pow(velocityY, 2.0))
    angle = Math.atan(velocityY / velocityX) + (velocityX < 0 ? Math.PI : 0.0)
    shellModel.state.position = nextPosition
    shellModel.state.velocity = velocity
    shellModel.state.angle = angle
  }
}
