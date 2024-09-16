import type { VehicleModel } from '@/data/model'

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

  getGunEndPosition(vehicleModel: VehicleModel) {
    const vehiclePosition = vehicleModel.state.position
    const gunAngle = vehicleModel.state.gunAngle
    const angle = vehicleModel.state.angle
    const gunLength = vehicleModel.config.gun.length
    return {
      x: vehiclePosition.x + gunLength * Math.cos(gunAngle + angle),
      y: vehiclePosition.y + gunLength * Math.sin(gunAngle + angle)
    }
  }
}
