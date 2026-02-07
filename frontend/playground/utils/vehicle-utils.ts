import type {RoomModel, VehicleModel} from '@/playground/data/model'
import {JetType, type Position} from '@/playground/data/common'
import {VehicleCalculations, type WheelCalculations} from '@/playground/data/calculations'
import {DefaultColors} from '~/dictionary/default-colors'
import {BattleUtils} from "~/playground/utils/battle-utils";

export const VehicleUtils = {
  /**
   * @deprecated
   */
  initVehicleCalculations(model: VehicleModel): VehicleCalculations {
    return new VehicleCalculations(model)
  },

  /**
   * @deprecated
   */
  calculateAllGroundContacts(vehicle: VehicleCalculations, roomModel: RoomModel) {
    vehicle.calculateAllGroundContacts(roomModel)
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

  getColor(userKey: string, currentUsername: string, vehicleModel: VehicleModel) {
    if (vehicleModel.config.color) {
      return vehicleModel.config.color
    }
    return DefaultColors.VEHICLE
  },

  isJetActive(vehicleModel: VehicleModel) {
    const jetState = vehicleModel.state.jetState
    if (!jetState) {
      return false
    }
    const jetType = vehicleModel.config.jet.type
    if (JetType.VERTICAL === jetType || this.isAboutToTurnOver(vehicleModel)) {
      return jetState.active && jetState.volume > 0
    }
    return jetState.active && jetState.volume > 0 && !!vehicleModel.state.movingDirection
  },

  isTurnedOver(vehicleModel: VehicleModel) {
    const angle = vehicleModel.state.position.angle
    return angle > Math.PI / 2 || angle < -Math.PI / 2
  },

  isAboutToTurnOver(vehicleModel: VehicleModel) {
    const angle = vehicleModel.state.position.angle
    const criticalAngle = 0.8 * Math.PI / 2
    return angle > criticalAngle || angle < -criticalAngle
  },

  /**
   * @deprecated
   */
  calculateGroundContact(wheel: WheelCalculations, wheelRadius: number, roomModel: RoomModel): void {
    wheel.calculateGroundContact(wheelRadius, roomModel)
  },
}
