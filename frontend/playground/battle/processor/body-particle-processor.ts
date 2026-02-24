import type {BodyParticleModel} from "@/playground/data/model";
import type {RoomSpecs} from "~/playground/data/specs";

export const BodyParticleProcessor = {
  processStep(particleModel: BodyParticleModel, timeStepSecs: number, roomSpecs: RoomSpecs) {
    if (particleModel.state.firstStepPassed) {
      const position = particleModel.state.position
      const velocity = particleModel.state.velocity
      const acceleration = particleModel.state.acceleration
      position.x += velocity.x * timeStepSecs
      position.y += velocity.y * timeStepSecs
      position.angle += velocity.angle * timeStepSecs
      velocity.y -= roomSpecs.gravityAcceleration * timeStepSecs
      if (acceleration) {
        velocity.x += acceleration.x * timeStepSecs * particleModel.state.remainTime
        velocity.y += acceleration.y * timeStepSecs * particleModel.state.remainTime
        velocity.angle += acceleration.angle * timeStepSecs * particleModel.state.remainTime
      }
      particleModel.state.remainTime -= timeStepSecs
    } else {
      particleModel.state.firstStepPassed = true
    }
  }
}
