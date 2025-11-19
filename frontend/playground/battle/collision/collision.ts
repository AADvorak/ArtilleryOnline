import type {Contact, Position, Vector, Velocity} from "~/playground/data/common";
import {Segment, VectorProjections} from "~/playground/data/geometry";
import type {BodyModel} from "~/playground/data/model";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BodyUtils} from "~/playground/utils/body-utils";
import {GeometryUtils} from "~/playground/utils/geometry-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";

export class ComponentData {
  constructor(
      public distanceToAxis: number,
      public inertiaToMassCoefficient: number,
      public rotationSign: -1 | 0 | 1,
      public resultMass: number
  ) {}
}

export class BodyCollisionData {
  velocity: Velocity
  velocityProjections: VectorProjections
  normalData: ComponentData
  tangentialData: ComponentData

  constructor(
      velocity: Velocity,
      velocityProjections: VectorProjections,
      normalData: ComponentData,
      tangentialData: ComponentData
  ) {
    this.velocity = velocity
    this.velocityProjections = velocityProjections
    this.normalData = normalData
    this.tangentialData = tangentialData
  }

  static of(bodyModel: BodyModel, contact: Contact, restrictRotation: boolean): BodyCollisionData {
    const comPosition = bodyModel.state.position
    const contactPos = contact.position
    const radiusNormalized = VectorUtils.vectorFromTo(comPosition, contact.position)
    VectorUtils.normalize(radiusNormalized)
    const velocityAtPosition = BodyUtils.getVelocityAt(bodyModel.state, contact.position)
    const tangential = VectorUtils.tangential(contact.angle)

    return new BodyCollisionData(
        velocityAtPosition,
        VectorProjections.of(velocityAtPosition, contact.angle),
        BodyCollisionData.getComponentDataInternal(bodyModel, contactPos, comPosition, contact.normal, radiusNormalized, restrictRotation),
        BodyCollisionData.getComponentDataInternal(bodyModel, contactPos, comPosition, tangential, radiusNormalized, restrictRotation)
    )
  }

  static getComponentData(bodyModel: BodyModel, component: Vector, contactPosition: Position): ComponentData {
    const comPosition = bodyModel.state.position
    const radiusNormalized = VectorUtils.vectorFromTo(comPosition, contactPosition)
    VectorUtils.normalize(radiusNormalized)
    return BodyCollisionData.getComponentDataInternal(bodyModel, contactPosition, comPosition, component, radiusNormalized, true)
  }

  private static getComponentDataInternal(
      bodyModel: BodyModel,
      contactPosition: Position,
      comPosition: Position,
      component: Vector,
      radiusNormalized: Vector,
      restrictRotation: boolean
  ): ComponentData {
    const radiusAndComponentVectorProduct = VectorUtils.vectorProduct(radiusNormalized, component)
    const distanceToAxis = BodyCollisionData.getDistanceToAxis(comPosition, contactPosition, component)

    const inertiaToMassCoefficient = restrictRotation
        ? BodyCollisionData.getInertiaToMassCoefficient(radiusAndComponentVectorProduct, distanceToAxis, bodyModel.preCalc.maxRadius)
        : Math.abs(radiusAndComponentVectorProduct)

    const resultMass = BodyCollisionData.getResultMass(bodyModel, distanceToAxis, inertiaToMassCoefficient)

    return new ComponentData(
        distanceToAxis,
        inertiaToMassCoefficient,
        Math.sign(radiusAndComponentVectorProduct) as -1 | 0 | 1,
        resultMass
    )
  }

  private static getInertiaToMassCoefficient(vectorProduct: number, distanceToAxis: number, maxRadius: number): number {
    let distanceCoefficient = 0

    if (distanceToAxis > maxRadius) {
      distanceCoefficient = 1
    } else {
      distanceCoefficient = Math.pow(distanceToAxis / maxRadius, 2)
    }

    return Math.abs(vectorProduct) * distanceCoefficient
  }

  private static getDistanceToAxis(comPosition: Position, contactPosition: Position, component: Vector): number {
    const axis = new Segment(contactPosition, VectorUtils.shifted(contactPosition, component))
    const axisProjection = GeometryUtils.getPointToLineProjection(comPosition, axis)
    return BattleUtils.distance(comPosition, axisProjection)
  }

  private static getResultMass(bodyModel: BodyModel, distanceToAxis: number, inertiaToMassCoefficient: number): number {
    const mass = bodyModel.preCalc.mass

    if (inertiaToMassCoefficient === 0) {
      return mass
    }

    const momentOfInertia = bodyModel.preCalc.momentOfInertia
    return (inertiaToMassCoefficient * momentOfInertia / Math.pow(distanceToAxis, 2) + mass) / (1 + inertiaToMassCoefficient)
  }
}
