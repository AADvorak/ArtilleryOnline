import type {ParticleModel} from "@/playground/data/model";
import type {RoomSpecs} from "~/playground/data/specs";

export const ParticleProcessor = {
  processStep(particleModel: ParticleModel, timeStepSecs: number, roomSpecs: RoomSpecs) {
    const position = particleModel.state.position
    const velocity = particleModel.state.velocity
    position.x += velocity.x * timeStepSecs
    position.y += velocity.y * timeStepSecs
    velocity.y -= roomSpecs.gravityAcceleration * timeStepSecs
    particleModel.state.remainTime -= timeStepSecs
  }
}
