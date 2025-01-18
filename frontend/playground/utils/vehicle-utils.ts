import type {VehicleModel} from '@/playground/data/model'
import {JetType, type Position} from '@/playground/data/common'
import type {WheelCalculations} from '@/playground/data/calculations'
import {DefaultColors} from '~/dictionary/default-colors'
import {useUserStore} from '~/stores/user'

export const VehicleUtils = {
  getLeftWheelPosition(vehicleModel: VehicleModel) {
    const position = vehicleModel.state.position
    const angle = vehicleModel.state.angle
    const wheelDistance = vehicleModel.preCalc.wheelDistance
    const wheelAngle = vehicleModel.preCalc.wheelAngle
    return {
      x: position.x - wheelDistance * Math.cos(angle + wheelAngle),
      y: position.y - wheelDistance * Math.sin(angle + wheelAngle)
    }
  },

  getLeftWheelBottomPosition(vehicleModel: VehicleModel) {
    return this.getWheelBottomPosition(this.getLeftWheelPosition(vehicleModel), vehicleModel, 0.8)
  },

  getLeftWheelTopPosition(vehicleModel: VehicleModel) {
    return this.getWheelTopPosition(this.getLeftWheelPosition(vehicleModel), vehicleModel, 0.8)
  },

  getRightWheelPosition(vehicleModel: VehicleModel) {
    const position = vehicleModel.state.position
    const angle = vehicleModel.state.angle
    const wheelDistance = vehicleModel.preCalc.wheelDistance
    const wheelAngle = vehicleModel.preCalc.wheelAngle
    return {
      x: position.x + wheelDistance * Math.cos(angle - wheelAngle),
      y: position.y + wheelDistance * Math.sin(angle - wheelAngle)
    }
  },

  getRightWheelBottomPosition(vehicleModel: VehicleModel) {
    return this.getWheelBottomPosition(this.getRightWheelPosition(vehicleModel), vehicleModel, 0.8)
  },

  getRightWheelTopPosition(vehicleModel: VehicleModel) {
    return this.getWheelTopPosition(this.getRightWheelPosition(vehicleModel), vehicleModel, 0.8)
  },

  getWheelBottomPosition(wheelPosition: Position, vehicleModel: VehicleModel, coefficient: number) {
    const angle = vehicleModel.state.angle - Math.PI / 2
    const wheelRadius = vehicleModel.specs.wheelRadius * coefficient
    return {
      x: wheelPosition.x + wheelRadius * Math.cos(angle),
      y: wheelPosition.y + wheelRadius * Math.sin(angle)
    }
  },

  getWheelTopPosition(wheelPosition: Position, vehicleModel: VehicleModel, coefficient: number) {
    const angle = vehicleModel.state.angle + Math.PI / 2
    const wheelRadius = vehicleModel.specs.wheelRadius * coefficient
    return {
      x: wheelPosition.x + wheelRadius * Math.cos(angle),
      y: wheelPosition.y + wheelRadius * Math.sin(angle)
    }
  },

  getGunEndPosition(vehicleModel: VehicleModel) {
    const vehiclePosition = vehicleModel.state.position
    const gunAngle = vehicleModel.state.gunAngle
    const angle = vehicleModel.state.angle
    const gunLength = vehicleModel.config.gun.length
    return {
      x: vehiclePosition.x + gunLength * Math.cos(gunAngle + angle),
      y: vehiclePosition.y + gunLength * Math.sin(gunAngle + angle)
    }
  },

  getSmallWheels(vehicleModel: VehicleModel) {
    const hullRadius = vehicleModel.specs.hullRadius
    const angle = vehicleModel.state.angle
    const leftWheelBottom = this.getWheelBottomPosition(
        this.getLeftWheelPosition(vehicleModel),
        vehicleModel,
        0.2
    )
    const smallWheels: Position[] = []
    for (let i = 1; i <= 3; i++) {
      smallWheels.push({
        x: leftWheelBottom.x + 0.5 * i * hullRadius * Math.cos(angle),
        y: leftWheelBottom.y + 0.5 * i * hullRadius * Math.sin(angle)
      })
    }
    return smallWheels
  },

  calculateWheelVelocity(vehicleModel: VehicleModel, wheelCalculations: WheelCalculations): void {
    const vehicleVelocity = vehicleModel.state.velocity
    const angle = vehicleModel.state.angle
    const angleVelocity = vehicleVelocity.angle * vehicleModel.specs.hullRadius
    const velocityX = vehicleVelocity.x + wheelCalculations.sign * angleVelocity * Math.sin(angle)
    const velocityY = vehicleVelocity.y - wheelCalculations.sign * angleVelocity * Math.cos(angle)
    wheelCalculations.velocity = {
      x: velocityX,
      y: velocityY
    }
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
