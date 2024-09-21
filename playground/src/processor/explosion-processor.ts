import type {ExplosionModel} from "@/data/model";

export const ExplosionProcessor = {
  processStep(explosionModel: ExplosionModel, timeStepSecs: number) {
    explosionModel.state.time += timeStepSecs
    explosionModel.state.radius = explosionModel.specs.radius
        * (explosionModel.specs.duration - explosionModel.state.time)
        / explosionModel.specs.duration
  }
}
