import type {BodyAcceleration} from '@/playground/data/common'
import type {BattleModel, RoomModel} from '@/playground/data/model'
import type {VehicleCalculations, WheelCalculations} from '@/playground/data/calculations'
import {VehicleUtils} from '@/playground/utils/vehicle-utils'
import {GroundContactUtils} from "~/playground/utils/ground-contact-utils";
import {Circle, HalfCircle} from "~/playground/data/geometry";
import {BodyAccelerationCalculator} from "~/playground/battle/calculator/body-acceleration-calculator";
import {GroundFrictionForceCalculator} from "~/playground/battle/calculator/vehicle/ground-friction-force-calculator";
import {GroundReactionForceCalculator} from "~/playground/battle/calculator/vehicle/ground-reaction-force-calculator";
import {GravityForceCalculator} from "~/playground/battle/calculator/vehicle/gravity-force-calculator";
import {JetForceCalculator} from "~/playground/battle/calculator/vehicle/jet-force-calculator";

export const VehicleAccelerationCalculator = {
  calculator: new BodyAccelerationCalculator<VehicleCalculations>(
      [
        new GravityForceCalculator(),
        new GroundFrictionForceCalculator(),
        new GroundReactionForceCalculator(),
        new JetForceCalculator()
      ]
  ),

  getVehicleAcceleration(vehicle: VehicleCalculations, battleModel: BattleModel): BodyAcceleration {
    VehicleUtils.calculateWheelVelocity(vehicle.model, vehicle.rightWheel)
    VehicleUtils.calculateWheelVelocity(vehicle.model, vehicle.leftWheel)

    const wheelRadius = vehicle.model.specs.wheelRadius
    this.calculateGroundContact(vehicle.rightWheel, wheelRadius, battleModel.room)
    this.calculateGroundContact(vehicle.leftWheel, wheelRadius, battleModel.room)
    this.calculateGroundContacts(vehicle, battleModel.room)

    return this.calculator.calculate(vehicle, battleModel)
  },

  calculateGroundContact(wheel: WheelCalculations, wheelRadius: number, roomModel: RoomModel): void {
    wheel.groundContact = GroundContactUtils.getCircleGroundContact(
        new Circle(wheel.position!, wheelRadius),
        roomModel,
        false
    )
  },

  calculateGroundContacts(vehicle: VehicleCalculations, roomModel: RoomModel): void {
    const position = VehicleUtils.getGeometryPosition(vehicle.model)
    const angle = vehicle.model.state.position.angle

    vehicle.groundContacts = GroundContactUtils.getHalfCircleGroundContacts(
        new HalfCircle(position, vehicle.model.specs.radius, angle),
        roomModel
    )
  }
}
