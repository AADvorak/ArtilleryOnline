import type {VehicleModel} from '@/playground/data/model'
import {JetType, type Position} from '@/playground/data/common'
import type {WheelCalculations} from '@/playground/data/calculations'
import {DefaultColors} from '~/dictionary/default-colors'
import {useUserStore} from '~/stores/user'
import {BattleUtils} from "~/playground/utils/battle-utils";
import {VectorUtils} from "~/playground/utils/vector-utils";

export const VehicleUtils = {
  getGeometryPosition(vehicleModel: VehicleModel) {
    const position = vehicleModel.state.position
    const comShift = vehicleModel.preCalc.centerOfMassShift
    return BattleUtils.shiftedPosition(position, - comShift.distance, position.angle + comShift.angle)
  },

  getLeftWheelPosition(vehicleModel: VehicleModel) {
    const position = vehicleModel.state.position
    const wheelDistance = vehicleModel.preCalc.wheelDistance
    const wheelAngle = vehicleModel.preCalc.wheelAngle
    return BattleUtils.shiftedPosition(position, - wheelDistance, position.angle + wheelAngle)
  },

  getLeftWheelBottomPosition(vehicleModel: VehicleModel) {
    return this.getWheelBottomPosition(this.getLeftWheelPosition(vehicleModel), vehicleModel, 0.8)
  },

  getLeftWheelTopPosition(vehicleModel: VehicleModel) {
    return this.getWheelTopPosition(this.getLeftWheelPosition(vehicleModel), vehicleModel, 0.8)
  },

  getRightWheelPosition(vehicleModel: VehicleModel) {
    const position = vehicleModel.state.position
    const wheelDistance = vehicleModel.preCalc.wheelDistance
    const wheelAngle = vehicleModel.preCalc.wheelAngle
    return BattleUtils.shiftedPosition(position, wheelDistance, position.angle - wheelAngle)
  },

  getRightWheelBottomPosition(vehicleModel: VehicleModel) {
    return this.getWheelBottomPosition(this.getRightWheelPosition(vehicleModel), vehicleModel, 0.8)
  },

  getRightWheelTopPosition(vehicleModel: VehicleModel) {
    return this.getWheelTopPosition(this.getRightWheelPosition(vehicleModel), vehicleModel, 0.8)
  },

  getWheelBottomPosition(wheelPosition: Position, vehicleModel: VehicleModel, coefficient: number) {
    const angle = vehicleModel.state.position.angle - Math.PI / 2
    const wheelRadius = vehicleModel.specs.wheelRadius * coefficient
    return BattleUtils.shiftedPosition(wheelPosition, wheelRadius, angle)
  },

  getWheelTopPosition(wheelPosition: Position, vehicleModel: VehicleModel, coefficient: number) {
    const angle = vehicleModel.state.position.angle + Math.PI / 2
    const wheelRadius = vehicleModel.specs.wheelRadius * coefficient
    return BattleUtils.shiftedPosition(wheelPosition, wheelRadius, angle)
  },

  getGunEndPosition(vehicleModel: VehicleModel) {
    const vehiclePosition = vehicleModel.state.position
    const gunAngle = vehicleModel.state.gunState.angle
    const gunLength = vehicleModel.config.gun.length
    return BattleUtils.shiftedPosition(vehiclePosition, gunLength, gunAngle + vehiclePosition.angle)
  },

  getSmallWheels(vehicleModel: VehicleModel) {
    const hullRadius = vehicleModel.specs.hullRadius
    const angle = vehicleModel.state.position.angle
    const leftWheelBottom = this.getWheelBottomPosition(
        this.getLeftWheelPosition(vehicleModel),
        vehicleModel,
        0.2
    )
    const smallWheels: Position[] = []
    for (let i = 1; i <= 3; i++) {
      smallWheels.push(BattleUtils.shiftedPosition(leftWheelBottom, 0.5 * i * hullRadius, angle))
    }
    return smallWheels
  },

  calculateWheelVelocity(vehicleModel: VehicleModel, wheelCalculations: WheelCalculations): void {
    const vehicleVelocity = vehicleModel.state.velocity
    const wheelAngle = vehicleModel.state.position.angle + Math.PI / 2 + wheelCalculations.sign * Math.PI / 2
    const wheelDistance = vehicleModel.preCalc.wheelDistance
    wheelCalculations.velocity = VectorUtils.getPointVelocity(vehicleVelocity, wheelDistance, wheelAngle)
  },

  getColor(userKey: string, vehicleModel: VehicleModel) {
    if (vehicleModel.config.color) {
      return vehicleModel.config.color
    }
    return userKey === useUserStore().user!.nickname ? DefaultColors.ALLY_VEHICLE : DefaultColors.ENEMY_VEHICLE
  },

  isJetActive(vehicleModel: VehicleModel) {
    const jetState = vehicleModel.state.jetState
    if (!jetState) {
      return false
    }
    const jetType = vehicleModel.config.jet.type
    if (JetType.VERTICAL === jetType) {
      return jetState.active && jetState.volume > 0
    }
    return jetState.active && jetState.volume > 0 && !!vehicleModel.state.movingDirection
  }
}
