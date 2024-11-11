import type {ShellHitType} from "~/playground/data/common";

export interface ShellHitEventObject {
  vehicleId: number
  type: ShellHitType
}

export interface ShellHitEvent {
  object: ShellHitEventObject
  shellId: number
}

export interface BattleModelEvents {
  hits: ShellHitEvent[]
}
