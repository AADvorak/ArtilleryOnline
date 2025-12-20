import {CollideObjectType, type Contact, type Position, type Vector, type Velocity} from "~/playground/data/common";
import {Segment, VectorProjections} from "~/playground/data/geometry";
import type {BodyModel} from "~/playground/data/model";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BodyUtils} from "~/playground/utils/body-utils";
import {GeometryUtils} from "~/playground/utils/geometry-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {
  BattleCalculations,
  BodyCalculationsBase,
  type Calculations,
  isBodyCalculationsImplementation
} from "~/playground/data/calculations";

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

export interface BodyCollisionDataPair {
  first: BodyCollisionData
  second?: BodyCollisionData
}

export interface CollisionPair {
  first: Calculations
  second?: Calculations
}

export interface VelocitiesProjections {
  first: VectorProjections
  second?: VectorProjections
}

export class Collision {
  private static readonly RESTITUTION = 0.5
  private static readonly GROUND_RESTITUTION = 0.1

  type: CollideObjectType
  pair: CollisionPair
  velocitiesProjections: VelocitiesProjections
  bodyCollisionDataPair: BodyCollisionDataPair
  contact: Contact
  impact: number | null = null
  hit = false

  constructor(
      type: CollideObjectType,
      pair: CollisionPair,
      velocitiesProjections: VelocitiesProjections,
      bodyCollisionDataPair: BodyCollisionDataPair,
      contact: Contact,
      hit = false
  ) {
    this.type = type
    this.pair = pair
    this.velocitiesProjections = velocitiesProjections
    this.bodyCollisionDataPair = bodyCollisionDataPair
    this.contact = contact
    this.hit = hit
  }

  closingVelocity(): number {
    return this.firstNormalVelocity() - this.secondNormalVelocity()
  }

  getImpact(): number {
    if (this.impact === null) {
      const closingVelocity = this.closingVelocity()

      if (closingVelocity <= 0.0) {
        this.impact = 0.0
      } else if (!this.pair.second) {
        const firstData = this.bodyCollisionDataPair.first
        const mass = firstData !== null ? firstData.normalData.resultMass : this.pair.first.getMass()
        this.impact = mass * closingVelocity * Collision.GROUND_RESTITUTION
      } else {
        const firstData = this.bodyCollisionDataPair.first
        const secondData = this.bodyCollisionDataPair.second
        const firstMass = !!firstData ? firstData.normalData.resultMass : this.pair.first.getMass()
        const secondMass = !!secondData ? secondData.normalData.resultMass : this.pair.second.getMass()
        this.impact = firstMass * secondMass * closingVelocity * (1 + this.restitution)
            / (firstMass + secondMass)
      }
    }
    return this.impact
  }

  sumKineticEnergy(): number {
    let sumEnergy = this.pair.first.getKineticEnergy()
    if (this.pair.second) {
      sumEnergy += this.pair.second.getKineticEnergy()
    }
    return sumEnergy
  }

  static withGround(first: Calculations, contact: Contact): Collision {
    return this.withUnmovable(first, contact, CollideObjectType.GROUND)
  }

  static withWall(first: Calculations, contact: Contact): Collision {
    return this.withUnmovable(first, contact, CollideObjectType.WALL)
  }

  static withSurface(first: Calculations, contact: Contact): Collision {
    return this.withUnmovable(first, contact, CollideObjectType.SURFACE)
  }

  private firstNormalVelocity(): number {
    return this.bodyCollisionDataPair.first !== null
        ? this.bodyCollisionDataPair.first.velocityProjections.normal
        : this.velocitiesProjections.first.normal
  }

  private secondNormalVelocity(): number {
    if (this.bodyCollisionDataPair.second) {
      return this.bodyCollisionDataPair.second.velocityProjections.normal
    } else if (this.velocitiesProjections.second) {
      return this.velocitiesProjections.second.normal
    }
    return 0
  }

  private get restitution(): number {
    return Collision.RESTITUTION
  }

  private static withUnmovable(
      first: Calculations,
      contact: Contact,
      type: CollideObjectType
  ): Collision {
    let firstData: BodyCollisionData | null = null
    if (isBodyCalculationsImplementation(first)) {
      const bodyCalculations = first as BodyCalculationsBase
      firstData = BodyCollisionData.of(bodyCalculations.getModel(), contact, false)
    }

    const velocityProjections = VectorProjections.of(first.getVelocity(), contact.angle)

    return new Collision(
        type,
        {first},
        {first: velocityProjections},
        {first: firstData!},
        contact
    )
  }
}

export interface CollisionsDetector {
  detect: (calculations: Calculations, battle: BattleCalculations) => Set<Collision>
}

export interface CollisionPreprocessor {
  process: (collision: Collision, battle: BattleCalculations) => boolean
}

export interface CollisionsPostprocessor {
  process: (calculations: Calculations, battle: BattleCalculations) => void
}
