import type {RoomModel, VehicleModel} from '@/playground/data/model'
import {JetType, MovingDirection, type Position, type Velocity} from '@/playground/data/common'
import {type VehicleCalculations, type WheelCalculations, WheelSign} from '@/playground/data/calculations'
import {DefaultColors} from '~/dictionary/default-colors'
import {useUserStore} from '~/stores/user'
import {BattleUtils} from "~/playground/utils/battle-utils";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {GroundContactUtils} from "~/playground/utils/ground-contact-utils";
import {Circle, HalfCircle, Trapeze} from "~/playground/data/geometry";
import {type HalfCircleShape, ShapeNames, type TrapezeShape} from "~/playground/data/shapes";
import {BodyUtils} from "~/playground/utils/body-utils";

export const VehicleUtils = {
  initVehicleCalculations(model: VehicleModel): VehicleCalculations {
    const calculations = {
      model,
      nextPosition: undefined,
      rightWheel: this.initWheelCalculations(WheelSign.RIGHT, VehicleUtils.getRightWheelPosition(model)),
      leftWheel: this.initWheelCalculations(WheelSign.LEFT, VehicleUtils.getLeftWheelPosition(model))
    }
    this.calculateWheelsVelocities(calculations)
    return calculations
  },

  initWheelCalculations(sign: WheelSign, position: Position): WheelCalculations {
    return {
      position,
      velocity: undefined,
      groundContact: null,
      sign,
    }
  },

  calculateAllGroundContacts(vehicle: VehicleCalculations, roomModel: RoomModel) {
    const wheelRadius = vehicle.model.specs.wheelRadius
    this.calculateGroundContact(vehicle.rightWheel, wheelRadius, roomModel)
    this.calculateGroundContact(vehicle.leftWheel, wheelRadius, roomModel)
    this.calculateGroundContacts(vehicle, roomModel)
  },

  calculateWheelsVelocities(vehicle: VehicleCalculations) {
    this.calculateWheelVelocity(vehicle.model, vehicle.rightWheel)
    this.calculateWheelVelocity(vehicle.model, vehicle.leftWheel)
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
    if (JetType.VERTICAL === jetType || this.isTurnedOver(vehicleModel)) {
      return jetState.active && jetState.volume > 0
    }
    return jetState.active && jetState.volume > 0 && !!vehicleModel.state.movingDirection
  },

  isTurnedOver(vehicleModel: VehicleModel) {
    const angle = vehicleModel.state.position.angle
    return angle > Math.PI / 2 || angle < -Math.PI / 2
  },

  calculateGroundContact(wheel: WheelCalculations, wheelRadius: number, roomModel: RoomModel): void {
    wheel.groundContact = GroundContactUtils.getCircleGroundContact(
        new Circle(wheel.position!, wheelRadius),
        roomModel,
        false
    )
  },

  calculateGroundContacts(vehicle: VehicleCalculations, roomModel: RoomModel): void {
    const position = BodyUtils.getGeometryBodyPosition(vehicle.model)
    const turretShape = vehicle.model.specs.turretShape

    if (turretShape.name === ShapeNames.HALF_CIRCLE) {
      vehicle.groundContacts = GroundContactUtils.getHalfCircleGroundContacts(
          HalfCircle.of(position, (turretShape as HalfCircleShape).radius),
          roomModel
      )
    }

    if (turretShape.name === ShapeNames.TRAPEZE) {
      vehicle.groundContacts = GroundContactUtils.getTrapezeGroundContacts(
          new Trapeze(position, turretShape as TrapezeShape),
          roomModel
      )
    }
  },

  getWheelVelocityAt(wheelCalculations: WheelCalculations, vehicleModel: VehicleModel, position: Position): Velocity {
    let angleVelocity = 0.0
    const movingDirection = vehicleModel.state.movingDirection
    var angleVelocitySpecs = vehicleModel.specs.wheelAngleVelocity
    if (MovingDirection.RIGHT === movingDirection) {
      angleVelocity = - angleVelocitySpecs
    }
    if (MovingDirection.LEFT === movingDirection) {
      angleVelocity = angleVelocitySpecs
    }
    const velocityAt = {
      x: wheelCalculations.velocity!.x,
      y: wheelCalculations.velocity!.y
    }
    if (angleVelocity != 0.0) {
      const angle = VectorUtils.angleFromTo(wheelCalculations.position!, position)
      const distance = BattleUtils.distance(wheelCalculations.position!, position)
      velocityAt.x -= angleVelocity * distance * Math.sin(angle)
      velocityAt.y += angleVelocity * distance * Math.cos(angle)
    }
    return velocityAt
  },
}
