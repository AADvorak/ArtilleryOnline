import type {BattleModel, VehicleModel} from '@/playground/data/model'
import {MovingDirection} from '@/playground/data/common'
import {type VehicleCalculations} from '@/playground/data/calculations'
import {VehicleUtils} from "~/playground/utils/vehicle-utils";
import {BodyAccelerationCalculator} from "~/playground/battle/calculator/body-acceleration-calculator";
import {GravityForceCalculator} from "~/playground/battle/calculator/common/gravity-force-calculator";
import {WheelsGroundFrictionForceCalculator} from "~/playground/battle/calculator/vehicle/wheels-ground-friction-force-calculator";
import {WheelsGroundReactionForceCalculator} from "~/playground/battle/calculator/vehicle/wheels-ground-reaction-force-calculator";
import {JetForceCalculator} from "~/playground/battle/calculator/vehicle/jet-force-calculator";
import {EngineForceCalculator} from "~/playground/battle/calculator/vehicle/engine-force-calculator";
import {BodyVelocityCalculator} from "~/playground/battle/calculator/body-velocity-calculator";
import {GroundFrictionForceCalculator} from "~/playground/battle/calculator/common/ground-friction-force-calculator";
import {GroundReactionForceCalculator} from "~/playground/battle/calculator/common/ground-reaction-force-calculator";
import {BodyUtils} from "~/playground/utils/body-utils";

const UNDER_GROUND_DEPTH_COEFFICIENT = 1.5

export const VehicleProcessor = {

  velocityCalculator: new BodyVelocityCalculator(
      new BodyAccelerationCalculator<VehicleCalculations>(
          [
            new GravityForceCalculator(),
            new GroundFrictionForceCalculator(),
            new WheelsGroundFrictionForceCalculator(),
            new GroundReactionForceCalculator(),
            new WheelsGroundReactionForceCalculator(),
            new JetForceCalculator(),
            new EngineForceCalculator()
          ]
      )
  ),

  processStep(vehicleModel: VehicleModel, battleModel: BattleModel, timeStepSecs: number) {
    const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
    this.recalculateVelocity(calculations, battleModel, timeStepSecs)
    const backupPosition = BodyUtils.getBackupPosition(vehicleModel)
    BodyUtils.recalculateBodyPosition(vehicleModel, timeStepSecs)
    if (this.checkUnderGround(calculations, battleModel)) {
      vehicleModel.state.position = backupPosition
    }
    this.recalculateGunAngle(vehicleModel, timeStepSecs)
    if (vehicleModel.state.gunState.loadingShell) {
      vehicleModel.state.gunState.loadRemainTime -= timeStepSecs
    }
    if (vehicleModel.state.jetState && vehicleModel.state.jetState.volume < vehicleModel.config.jet.capacity) {
      vehicleModel.state.jetState.volume += (VehicleUtils.isJetActive(vehicleModel)
          ? -vehicleModel.config.jet.consumption
          : vehicleModel.config.jet.regeneration) * timeStepSecs
    }
  },

  recalculateVelocity(
      calculations: VehicleCalculations,
      battleModel: BattleModel,
      timeStepSecs: number
  ) {
    VehicleUtils.calculateAllGroundContacts(calculations, battleModel.room)
    this.velocityCalculator.recalculateVelocity(calculations, battleModel, timeStepSecs)
  },

  recalculateGunAngle(vehicleModel: VehicleModel, timeStepSecs: number) {
    const gunState = vehicleModel.state.gunState
    const rotatingDirection = gunState.rotatingDirection
    const rotatingVelocity = vehicleModel.config.gun.rotationVelocity
    const maxGunAngle = vehicleModel.specs.maxAngle
    const minGunAngle = vehicleModel.specs.minAngle
    const vehicleAngle = vehicleModel.state.position.angle
    let targetAngle = gunState.fixed
        ? vehicleAngle + gunState.angle
        : gunState.targetAngle
    if (rotatingDirection) {
      const sign = MovingDirection.RIGHT === rotatingDirection ? -1 : 1
      targetAngle += sign * rotatingVelocity * timeStepSecs
    }
    targetAngle = this.restrictValue(targetAngle, minGunAngle + vehicleAngle, maxGunAngle + vehicleAngle)
    gunState.targetAngle = targetAngle
    if (gunState.fixed) {
      gunState.angle = targetAngle - vehicleAngle
    } else {
      let gunAngle = gunState.angle
      const angleDiff = targetAngle - vehicleAngle - gunAngle
      const angleStep = Math.sign(angleDiff) * rotatingVelocity * timeStepSecs
      if (Math.abs(angleDiff) > Math.abs(angleStep)) {
        gunAngle += angleStep
        gunAngle = this.restrictValue(gunAngle, minGunAngle, maxGunAngle)
        gunState.angle = gunAngle
      }
    }
  },

  restrictValue(value: number, min: number, max: number) {
    if (value < min) {
      return min
    }
    return Math.min(value, max)
  },

  checkUnderGround(calculations: VehicleCalculations, battleModel: BattleModel) {
    calculations.rightWheel.position = VehicleUtils.getRightWheelPosition(calculations.model)
    calculations.leftWheel.position = VehicleUtils.getLeftWheelPosition(calculations.model)
    VehicleUtils.calculateAllGroundContacts(calculations, battleModel.room)
    for (const contact of BodyUtils.getAllGroundContacts(calculations)) {
      if (contact.depth > battleModel.room.specs.groundMaxDepth * UNDER_GROUND_DEPTH_COEFFICIENT) {
        return true
      }
    }
    return false
  }
}
