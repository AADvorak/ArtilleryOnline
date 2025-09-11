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

export interface RepairEvent {
  vehicleId: number
}

export interface BattleModelEvents {
  hits?: ShellHitEvent[]
  collides?: CollideEvent[]
  ricochets?: RicochetEvent[]
  bomberFlyEvents?: BomberFlyEvent[]
  repairs?: RepairEvent[]
}
