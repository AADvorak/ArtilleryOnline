import {type CollideObject, CollideObjectType, MovingDirection, type ShellHitType} from "~/playground/data/common";

export interface ShellHitEventObject {
  vehicleId?: number
  type: ShellHitType
}

export interface ShellHitEvent {
  object: ShellHitEventObject
  shellId: number
}

export interface CollideEvent {
  id: number
  type: CollideObjectType
  object: CollideObject
}

export interface RicochetEvent {
  shellId: number
}

export interface BomberFlyEvent {
  movingDirection: MovingDirection
}

export enum RepairEventType {
  TRACK = 'TRACK',
  TURN_ON_WHEELS = 'TURN_ON_WHEELS',
  HEAL = 'HEAL'
}

export interface RepairEvent {
  vehicleId: number
  type: RepairEventType
}

export interface BattleModelEvents {
  hits?: ShellHitEvent[]
  collides?: CollideEvent[]
  ricochets?: RicochetEvent[]
  bomberFlyEvents?: BomberFlyEvent[]
  repairs?: RepairEvent[]
}
