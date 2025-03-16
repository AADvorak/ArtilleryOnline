import {type CollideObject, MovingDirection, type ShellHitType} from "~/playground/data/common";

export interface ShellHitEventObject {
  vehicleId?: number
  type: ShellHitType
}

export interface ShellHitEvent {
  object: ShellHitEventObject
  shellId: number
}

export interface VehicleCollideEvent {
  vehicleId: number
  object: CollideObject
}

export interface RicochetEvent {
  shellId: number
}

export interface BomberFlyEvent {
  movingDirection: MovingDirection
}

export interface BattleModelEvents {
  hits?: ShellHitEvent[]
  collides?: VehicleCollideEvent[]
  ricochets?: RicochetEvent[]
  bomberFlyEvents?: BomberFlyEvent[]
}
