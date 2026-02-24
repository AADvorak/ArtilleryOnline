import type {BodyParticleModel} from "@/playground/data/model";
import type {RoomSpecs} from "~/playground/data/specs";

export const BodyParticleProcessor = {
  processStep(particleModel: BodyParticleModel, timeStepSecs: number, roomSpecs: RoomSpecs) {
    if (particleModel.state.firstStepPassed) {
      const position = particleModel.state.position
      const velocity = particleModel.state.velocity
      position.x += velocity.x * timeStepSecs
      position.y += velocity.y * timeStepSecs
      position.angle += velocity.angle * timeStepSecs
      velocity.y -= roomSpecs.gravityAcceleration * timeStepSecs
      particleModel.state.remainTime -= timeStepSecs
    } else {
      particleModel.state.firstStepPassed = true
    }
  }
}
