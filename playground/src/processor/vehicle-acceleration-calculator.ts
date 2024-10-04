import {
  type Acceleration,
  MovingDirection,
  type Position,
  type VehicleAcceleration,
  type Velocity
} from '@/data/common'
import type { RoomModel, VehicleModel } from '@/data/model'
import type {
  NearestGroundPoint,
  VehicleCalculations,
  WheelCalculations
} from '@/data/calculations'
import { BattleUtils } from '@/utils/battle-utils'
import { VehicleUtils } from '@/utils/vehicle-utils'
import { VectorUtils } from '@/utils/vector-utils'

export const VehicleAccelerationCalculator = {
  getVehicleAcceleration(
    calculations: VehicleCalculations,
    vehicleModel: VehicleModel,
    roomModel: RoomModel
  ): VehicleAcceleration {
    const angle = vehicleModel.state.angle

    VehicleUtils.calculateWheelVelocity(vehicleModel, calculations.rightWheel)
    VehicleUtils.calculateWheelVelocity(vehicleModel, calculations.leftWheel)

    calculations.rightWheel.position = VehicleUtils.getRightWheelPosition(vehicleModel)
    calculations.leftWheel.position = VehicleUtils.getLeftWheelPosition(vehicleModel)

    this.calculateWheelAcceleration(calculations.rightWheel, vehicleModel, roomModel)
    this.calculateWheelAcceleration(calculations.leftWheel, vehicleModel, roomModel)

    this.calculateWheelSumAcceleration(calculations.rightWheel)
    this.calculateWheelSumAcceleration(calculations.leftWheel)

    const rotatingAcceleration = this.getVehicleRotatingAcceleration(calculations, angle)
    const wheelsMovingAcceleration = {
      x:
        (calculations.rightWheel.sumAcceleration!.x + calculations.leftWheel.sumAcceleration!.x) / 2,
      y:
        (calculations.rightWheel.sumAcceleration!.y + calculations.leftWheel.sumAcceleration!.y) / 2
    }

    const vehicleVelocity = vehicleModel.state.velocity
    const frictionCoefficient = vehicleModel.preCalc.frictionCoefficient
    const frictionAcceleration = {
      x: -vehicleVelocity.x * Math.abs(vehicleVelocity.x) * frictionCoefficient,
      y: -vehicleVelocity.y * Math.abs(vehicleVelocity.y) * frictionCoefficient
    }

    const movingAcceleration = VectorUtils.sumOf(wheelsMovingAcceleration, frictionAcceleration)

    return {
      x: movingAcceleration.x,
      y: movingAcceleration.y,
      angle: rotatingAcceleration / vehicleModel.specs.radius - vehicleVelocity.angle
    }
  },

  calculateWheelSumAcceleration(wheelCalculations: WheelCalculations): void {
    wheelCalculations.sumAcceleration = VectorUtils.sumOf(
      wheelCalculations.gravityAcceleration,
      wheelCalculations.engineAcceleration,
      wheelCalculations.groundFrictionAcceleration,
      wheelCalculations.groundReactionAcceleration
    )
  },

  getVehicleRotatingAcceleration(calculations: VehicleCalculations, angle: number): number {
    const rightWheelRotatingAcceleration =
      calculations.rightWheel.sumAcceleration!.x * Math.sin(angle) +
      calculations.rightWheel.sumAcceleration!.y * Math.cos(angle)
    const leftWheelRotatingAcceleration =
      calculations.leftWheel.sumAcceleration!.x * Math.sin(angle) +
      calculations.leftWheel.sumAcceleration!.y * Math.cos(angle)
    return (rightWheelRotatingAcceleration - leftWheelRotatingAcceleration) / 2
  },

  calculateWheelAcceleration(
    wheelCalculations: WheelCalculations,
    vehicleModel: VehicleModel,
    roomModel: RoomModel
  ): void {
    const roomGravityAcceleration = roomModel.specs.gravityAcceleration
    const groundReactionCoefficient = roomModel.specs.groundReactionCoefficient
    const groundFrictionCoefficient = roomModel.specs.groundFrictionCoefficient
    const wheelRadius = vehicleModel.specs.wheelRadius

    wheelCalculations.nearestGroundPointByX = BattleUtils.getNearestGroundPosition(
      wheelCalculations.position!.x,
      roomModel
    )

    this.calculateNearestGroundPointAngleAndDepth(wheelCalculations, wheelRadius, roomModel)

    if (wheelCalculations.nearestGroundPointByX.y >= wheelCalculations.position!.y) {
      const depth = wheelCalculations.depth != null ? wheelCalculations.depth : 2 * wheelRadius
      wheelCalculations.groundFrictionAcceleration = this.getInGroundFrictionAcceleration(
        wheelCalculations.velocity!,
        depth,
        groundFrictionCoefficient
      )

      if (wheelCalculations.nearestGroundPoint != null) {
        wheelCalculations.groundReactionAcceleration = this.getGroundReactionAcceleration(
          wheelCalculations.velocity!,
          wheelCalculations.groundAngle!,
          depth,
          groundReactionCoefficient
        )
      }
      return
    }

    if (wheelCalculations.nearestGroundPoint == null) {
      wheelCalculations.gravityAcceleration = {
        x: 0.0,
        y: -roomGravityAcceleration
      }
      return
    }

    if (wheelCalculations.depth! <= roomModel.specs.groundMaxDepth) {
      wheelCalculations.gravityAcceleration = this.getOnGroundGravityAcceleration(
        roomGravityAcceleration,
        wheelCalculations.groundAngle!
      )
    }
    wheelCalculations.groundReactionAcceleration = this.getGroundReactionAcceleration(
      wheelCalculations.velocity!,
      wheelCalculations.groundAngle!,
      wheelCalculations.depth!,
      groundReactionCoefficient
    )
    wheelCalculations.groundFrictionAcceleration = this.getInGroundFrictionAcceleration(
      wheelCalculations.velocity!,
      wheelCalculations.depth!,
      groundFrictionCoefficient
    )
    wheelCalculations.engineAcceleration = this.getWheelEngineAcceleration(
      vehicleModel,
      wheelCalculations.groundAngle!,
      wheelCalculations.depth!
    )
  },

  calculateNearestGroundPointAngleAndDepth(
    wheelCalculations: WheelCalculations,
    wheelRadius: number,
    roomModel: RoomModel
  ): void {
    const nearestGroundPoint = this.getNearestGroundPoint(
      wheelCalculations.position!,
      wheelRadius,
      roomModel,
      wheelCalculations.sign
    )
    if (nearestGroundPoint == null) {
      return
    }
    wheelCalculations.nearestGroundPoint = nearestGroundPoint
    wheelCalculations.groundAngle = this.getGroundAngle(
      wheelCalculations.position!,
      nearestGroundPoint,
      roomModel
    )
    if (nearestGroundPoint.position.y <= wheelCalculations.position!.y) {
      wheelCalculations.depth = wheelRadius - nearestGroundPoint.distance
    } else {
      wheelCalculations.depth = wheelRadius + nearestGroundPoint.distance
    }
  },

  getGroundAngle(
    position: Position,
    nearestGroundPoint: NearestGroundPoint,
    roomModel: RoomModel
  ): number {
    if (nearestGroundPoint.index > 0 && nearestGroundPoint.position.x <= position.x) {
      const otherGroundPosition = BattleUtils.getGroundPosition(
        nearestGroundPoint.index - 1,
        roomModel
      )
      return Math.atan(
        (nearestGroundPoint.position.y - otherGroundPosition.y) /
          (nearestGroundPoint.position.x - otherGroundPosition.x)
      )
    } else {
      const otherGroundPosition = BattleUtils.getGroundPosition(
        nearestGroundPoint.index + 1,
        roomModel
      )
      return Math.atan(
        (otherGroundPosition.y - nearestGroundPoint.position.y) /
          (otherGroundPosition.x - nearestGroundPoint.position.x)
      )
    }
  },

  getOnGroundGravityAcceleration(
    roomGravityAcceleration: number,
    groundAngle: number
  ): Acceleration {
    const groundAccelerationModule = Math.abs(roomGravityAcceleration * Math.sin(groundAngle))
    return {
      x: -groundAccelerationModule * Math.sin(groundAngle),
      y: -groundAccelerationModule * Math.cos(groundAngle)
    }
  },

  getGroundReactionAcceleration(
    velocity: Velocity,
    groundAngle: number,
    depth: number,
    coefficient: number
  ): Acceleration {
    const velocityVerticalProjection = VectorUtils.getVerticalProjection(velocity, groundAngle)
    if (velocityVerticalProjection >= 0) {
      return { x: 0, y: 0 }
    } else {
      const accelerationVerticalProjection = -velocityVerticalProjection * depth * coefficient
      return {
        x: VectorUtils.getComponentX(accelerationVerticalProjection, 0.0, groundAngle),
        y: VectorUtils.getComponentY(accelerationVerticalProjection, 0.0, groundAngle)
      }
    }
  },

  getInGroundFrictionAcceleration(
    velocity: Velocity,
    depth: number,
    coefficient: number
  ): Acceleration {
    return {
      x: -velocity.x * depth * coefficient,
      y: -velocity.y * depth * coefficient
    }
  },

  getWheelEngineAcceleration(
    vehicleModel: VehicleModel,
    angle: number,
    depth: number
  ): Acceleration {
    if (vehicleModel.state.trackState.broken) {
      return { x: 0, y: 0 }
    }

    const direction = vehicleModel.state.movingDirection
    const wheelRadius = vehicleModel.specs.wheelRadius
    const depthCoefficient = 1 - (depth * 0.5) / wheelRadius
    const acceleration = (depthCoefficient * vehicleModel.specs.acceleration) / 2
    const depthAngle = (depth * Math.PI) / (4 * wheelRadius)

    if (MovingDirection.RIGHT === direction) {
      return {
        x: acceleration * Math.cos(angle + depthAngle),
        y: acceleration * Math.sin(angle + depthAngle)
      }
    }

    if (MovingDirection.LEFT === direction) {
      return {
        x: -acceleration * Math.cos(angle - depthAngle),
        y: -acceleration * Math.sin(angle - depthAngle)
      }
    }

    return { x: 0, y: 0 }
  },

  getNearestGroundPoint(
    objectPosition: Position,
    objectRadius: number,
    roomModel: RoomModel,
    sign: number
  ): NearestGroundPoint | undefined {
    const groundIndexes = BattleUtils.getGroundIndexesBetween(
      objectPosition.x - objectRadius,
      objectPosition.x + objectRadius,
      roomModel
    )

    if (groundIndexes.length === 0) {
      return undefined
    }

    let nearestPosition: Position | undefined
    let minimalDistance: number | undefined
    let index: number | undefined
    let i = sign > 0 ? 0 : groundIndexes.length - 1

    while (i >= 0 && i < groundIndexes.length) {
      const position = BattleUtils.getGroundPosition(groundIndexes[i], roomModel)
      const distance = BattleUtils.distance(position, objectPosition)

      if (distance <= objectRadius) {
        if (!minimalDistance || distance < minimalDistance) {
          nearestPosition = position
          minimalDistance = distance
          index = groundIndexes[i]
        }
      }
      i += sign
    }

    if (!nearestPosition) {
      return undefined
    }

    return { position: nearestPosition, distance: minimalDistance!, index: index! }
  }
}
