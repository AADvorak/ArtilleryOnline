import type {BaseModel} from "~/playground/data/model";

export const BaseProcessor = {
  processStep(baseModel: BaseModel, timeStepSecs: number) {
    const capturePoints = baseModel.state.capturePoints
    Object.keys(baseModel.state.capturePoints).forEach(nickname => {
      if (capturePoints[nickname]) {
        capturePoints[nickname] = capturePoints[nickname] + baseModel.specs.captureRate * timeStepSecs
      }
    })
  }
}
