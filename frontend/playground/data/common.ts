import {VectorUtils} from "~/playground/utils/vector-utils";
import {Constants} from "~/playground/data/constants";

export interface Size {
  width: number
  height: number
}

export interface Ammo {
  [key: string]: number
}

export interface Missiles {
  [key: string]: number
}

export enum MovingDirection {
  LEFT = 'LEFT',
  RIGHT = 'RIGHT',
  UP = 'UP',
  DOWN = 'DOWN'
}

export enum ShellType {
  AP = 'AP',
  HE = 'HE',
  SGN = 'SGN',
  BMB = 'BMB'
}

export enum JetType {
  VERTICAL = 'VERTICAL',
  HORIZONTAL = 'HORIZONTAL'
}

export enum ShellHitType {
  GROUND = 'GROUND',
  VEHICLE_HULL = 'VEHICLE_HULL',
  VEHICLE_TRACK = 'VEHICLE_TRACK',
  DRONE = 'DRONE',
  BOX = 'BOX',
}

export enum HitSurface {
  TOP = 'TOP',
  BOTTOM = 'BOTTOM',
  SIDE = 'SIDE',
}

export enum CollideObjectType {
  WALL = 'WALL',
  GROUND = 'GROUND',
  VEHICLE = 'VEHICLE',
  BOX = 'BOX',
  SURFACE = 'SURFACE'
}

export enum BoxType {
  HP = 'HP',
  AMMO = 'AMMO'
}

export interface CollideObject {
  type: CollideObjectType
  id?: number
}

export interface ApplicationSettings {
  additionalResolveCollisionsIterationsNumber: number
  debug: boolean
  showShellTrajectory: boolean
  clientProcessing: boolean
  clientCollisionsProcessing: boolean
  clientSmoothTransition: boolean
  userBattleQueueTimeout: number
}

export interface TimeZone {
  offset: number
}

export interface Vector {
  x: number
  y: number
}

export interface BodyVector extends Vector {
  angle: number
}

export interface Force extends Vector {
}

export interface Acceleration extends Vector {
}

export interface Velocity extends Vector {
}

export interface Position extends Vector {
}

export interface BodyVelocity extends BodyVector {
}

export interface BodyAcceleration extends BodyVector{
}

export interface BodyPosition extends BodyVector {
}

export interface Shift {
  distance: number
  angle: number
}

export class Contact {
  depth: number
  angle: number
  normal: Vector
  position: Position
  description?: string

  constructor(
      depth: number,
      angle: number,
      normal: Vector,
      position: Position,
      description?: string
  ) {
    this.depth = depth
    this.angle = angle
    this.normal = normal
    this.position = position
    this.description = description
  }

  inverted() {
    return new Contact(this.depth, this.angle - Math.PI, VectorUtils.inverted(this.normal),
        this.position, this.description)
  }

  static withAngle(depth: number, angle: number, position: Position, description?: string): Contact | null {
    return this.checkDepth(new Contact(
        depth,
        angle,
        VectorUtils.normal(angle),
        position,
        description
    ))
  }

  static withNormal(depth: number, normal: Vector, position: Position, description?: string): Contact | null {
    return this.checkDepth(new Contact(
        depth,
        VectorUtils.getAngle(normal) + Math.PI / 2,
        normal,
        position,
        description
    ))
  }

  static withAngleUncheckedDepth(depth: number, angle: number, position: Position, description?: string): Contact {
    return new Contact(
        depth,
        angle,
        VectorUtils.normal(angle),
        position,
        description
    )
  }

  static withNormalUncheckedDepth(depth: number, normal: Vector, position: Position, description?: string): Contact {
    return new Contact(
        depth,
        VectorUtils.getAngle(normal) + Math.PI / 2,
        normal,
        position,
        description
    )
  }

  static checkDepth(contact: Contact): Contact | null {
    if (contact.depth < Constants.INTERPENETRATION_THRESHOLD) {
      return null
    }
    return contact
  }
}

export function zeroVector(): Vector {
  return {
    x: 0,
    y: 0
  }
}

export function zeroBodyVector(): BodyVector {
  return {
    x: 0,
    y: 0,
    angle: 0
  }
}

export function cloneVector(vector: Vector): Vector {
  return {
    x: vector.x,
    y: vector.y
  }
}
