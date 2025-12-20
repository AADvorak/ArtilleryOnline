import {Collision, type CollisionsDetector} from "~/playground/battle/collision/collision";
import {
  BattleCalculations,
  type Calculations,
  VehicleCalculations,
  WheelCalculations
} from "~/playground/data/calculations";
import {SurfaceContactUtils} from "~/playground/utils/surface-contact-utils";
import {Circle} from "~/playground/data/geometry";
import {VehicleUtils} from "~/playground/utils/vehicle-utils";

export class VehicleSurfaceCollisionsDetector implements CollisionsDetector {
  detect(calculations: Calculations, battle: BattleCalculations): Set<Collision> {
    if ('rightWheel' in calculations && 'leftWheel' in calculations) {
      const vehicle = calculations as VehicleCalculations
      return this.detectVehicleCollisions(vehicle, battle)
    }
    return new Set()
  }

  private detectVehicleCollisions(vehicle: VehicleCalculations, battle: BattleCalculations): Set<Collision> {
    const collisions = new Set<Collision>()
    const wheels = [vehicle.rightWheel, vehicle.leftWheel]
    wheels.forEach(wheel => {
      this.detectWheelCollisions(wheel, battle).forEach(collision => collisions.add(collision))
    })
    this.detectHullCollisions(vehicle, battle).forEach(collision => collisions.add(collision))
    return collisions
  }

  private detectWheelCollisions(wheel: WheelCalculations, battle: BattleCalculations): Collision[] {
    const circle = new Circle(wheel.nextPosition!, wheel.model.specs.wheelRadius)
    return SurfaceContactUtils.getContacts(circle, battle.model.room, true).values()
        .map(contact => Collision.withSurface(wheel, contact)).toArray()
  }

  private detectHullCollisions(vehicle: VehicleCalculations, battle: BattleCalculations): Collision[] {
    const bodyPart = VehicleUtils.getTurretBodyPart(
        vehicle.model.specs.turretShape,
        vehicle.getGeometryNextPosition()!
    )
    return SurfaceContactUtils.getContacts(bodyPart, battle.model.room, true).values()
        .map(contact => Collision.withSurface(vehicle, contact)).toArray()
  }
}
