import type {VehicleModel} from '@/playground/data/model'
import {MovingDirection} from '@/playground/data/common'
import {BattleCalculations, type VehicleCalculations} from '@/playground/data/calculations'
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
import type {GunState} from "~/playground/data/state";
import type {GunSpecs} from "~/playground/data/specs";

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

  processBeforeCollision(vehicle: VehicleCalculations, battle: BattleCalculations) {
    this.recalculateJetState(vehicle.model, battle.timeStepSecs)
    this.recalculateMissileLauncherState(vehicle.model, battle.timeStepSecs)
    this.recalculateBomberState(vehicle.model, battle.timeStepSecs)
    this.recalculateDroneState(vehicle.model, battle.timeStepSecs)
    vehicle.calculateAllGroundContacts(battle.model.room)
    this.velocityCalculator.recalculateVelocity(vehicle, battle.model, battle.timeStepSecs)
    vehicle.calculateNextPosition(battle.timeStepSecs)
    this.recalculateGunAngle(vehicle.model, battle.timeStepSecs)
    this.recalculateGunState(vehicle.model.state.gunState, battle.timeStepSecs)
  },

  processAfterCollision(vehicle: VehicleCalculations, battle: BattleCalculations) {
    vehicle.applyNextPosition()
  },

  recalculateGunAngle(vehicleModel: VehicleModel, timeStepSecs: number) {
    const gunState = vehicleModel.state.gunState
    const rotatingDirection = gunState.rotatingDirection
    const maxGunAngle = vehicleModel.specs.maxAngle
    const minGunAngle = vehicleModel.specs.minAngle
    const vehicleAngle = vehicleModel.state.position.angle
    let targetAngle = gunState.fixed
        ? vehicleAngle + gunState.angle
        : gunState.targetAngle
    if (rotatingDirection) {
      const sign = MovingDirection.RIGHT === rotatingDirection ? -1 : 1
      targetAngle += sign * this.getRotationVelocity(vehicleModel.config.gun, gunState.rotatingTime) * timeStepSecs
      gunState.rotatingTime += timeStepSecs
    }
    targetAngle = this.restrictValue(targetAngle, minGunAngle + vehicleAngle, maxGunAngle + vehicleAngle)
    gunState.targetAngle = targetAngle
    if (gunState.fixed) {
      gunState.angle = targetAngle - vehicleAngle
    } else {
      let gunAngle = gunState.angle
      const angleDiff = targetAngle - vehicleAngle - gunAngle
      const angleStep = Math.sign(angleDiff) * vehicleModel.config.gun.rotationVelocity * timeStepSecs
      if (Math.abs(angleDiff) > Math.abs(angleStep)) {
        gunAngle += angleStep
        gunAngle = this.restrictValue(gunAngle, minGunAngle, maxGunAngle)
        gunState.angle = gunAngle
      }
    }
  },

  recalculateGunState(gunState: GunState, timeStepSecs: number) {
    if (gunState.loadingShell) {
      gunState.loadRemainTime -= timeStepSecs
    }
  },

  recalculateJetState(vehicleModel: VehicleModel, timeStepSecs: number) {
    if (vehicleModel.state.jetState && vehicleModel.state.jetState.volume < vehicleModel.config.jet.capacity) {
      vehicleModel.state.jetState.volume += (VehicleUtils.isJetActive(vehicleModel)
          ? -vehicleModel.config.jet.consumption
          : vehicleModel.config.jet.regeneration) * timeStepSecs
    }
  },

  recalculateMissileLauncherState(vehicleModel: VehicleModel, timeStepSecs: number) {
    const state = vehicleModel.state.missileLauncherState
    if (state && state.prepareToLaunchRemainTime > 0 && state.remainMissiles > 0) {
      state.prepareToLaunchRemainTime -= timeStepSecs
    }
  },

  recalculateBomberState(vehicleModel: VehicleModel, timeStepSecs: number) {
    const state = vehicleModel.state.bomberState
    if (state && state.prepareToFlightRemainTime > 0 && state.remainFlights > 0) {
      state.prepareToFlightRemainTime -= timeStepSecs
    }
  },

  recalculateDroneState(vehicleModel: VehicleModel, timeStepSecs: number) {
    const state = vehicleModel.state.droneState
    if (state && !state.launched && !state.readyToLaunch && state.remainDrones > 0) {
      state.prepareToLaunchRemainTime -= timeStepSecs
    }
  },

  restrictValue(value: number, min: number, max: number) {
    if (value < min) {
      return min
    }
    return Math.min(value, max)
  },

  getRotationVelocity(gunSpecs: GunSpecs, time: number) {
    const minVelocity = gunSpecs.slowRotationVelocity
    const maxVelocity = gunSpecs.rotationVelocity
    const maxTime = gunSpecs.slowToFastRotationTime
    if (time > maxTime) {
      return maxVelocity
    } else {
      return minVelocity + (time - maxTime) * (maxVelocity - minVelocity)
    }
  }
}
