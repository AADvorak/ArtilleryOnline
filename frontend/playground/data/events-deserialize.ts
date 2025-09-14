import {
  type BattleModelEvents,
  type BomberFlyEvent,
  type RepairEvent,
  type RicochetEvent,
  type ShellHitEvent,
  type ShellHitEventObject,
  type CollideEvent, RepairEventType
} from "~/playground/data/events";
import type {DeserializerInput} from "~/deserialization/deserializer-input";
import {DeserializerBase} from "~/deserialization/deserializer-base";
import {type CollideObject, CollideObjectType, type MovingDirection, type ShellHitType} from "~/playground/data/common";

export function deserializeShellHitEventObject(input: DeserializerInput): ShellHitEventObject {
  const vehicleId = DeserializerBase.readNullable(input, DeserializerBase.readInt)
  const type = DeserializerBase.readString(input) as ShellHitType
  return {
    vehicleId,
    type
  }
}

export function deserializeShellHitEvent(input: DeserializerInput): ShellHitEvent {
  const object = deserializeShellHitEventObject(input)
  const shellId = DeserializerBase.readInt(input)
  return {
    object,
    shellId
  }
}

export function deserializeCollideObject(input: DeserializerInput): CollideObject {
  const id = DeserializerBase.readNullable(input, DeserializerBase.readInt)
  const type = DeserializerBase.readString(input) as CollideObjectType
  return {
    id,
    type
  }
}

export function deserializeVehicleCollideEvent(input: DeserializerInput): CollideEvent {
  const object = deserializeCollideObject(input)
  const id = DeserializerBase.readInt(input)
  const type = DeserializerBase.readString(input) as CollideObjectType
  return {
    object,
    id,
    type
  }
}

export function deserializeRicochetEvent(input: DeserializerInput): RicochetEvent {
  const shellId = DeserializerBase.readInt(input)
  return {
    shellId
  }
}

export function deserializeBomberFlyEvent(input: DeserializerInput): BomberFlyEvent {
  const movingDirection = DeserializerBase.readString(input) as MovingDirection
  return {
    movingDirection
  }
}

export function deserializeRepairEvent(input: DeserializerInput): RepairEvent {
  const vehicleId = DeserializerBase.readInt(input)
  const type = DeserializerBase.readString(input) as RepairEventType
  return {
    vehicleId,
    type
  }
}

export function deserializeBattleModelEvents(input: DeserializerInput): BattleModelEvents {
  const hits = DeserializerBase.readArray(input, deserializeShellHitEvent)
  const collides = DeserializerBase.readArray(input, deserializeVehicleCollideEvent)
  const ricochets = DeserializerBase.readArray(input, deserializeRicochetEvent)
  const bomberFlyEvents = DeserializerBase.readArray(input, deserializeBomberFlyEvent)
  const repairs = DeserializerBase.readArray(input, deserializeRepairEvent)
  return {
    hits,
    collides,
    ricochets,
    bomberFlyEvents,
    repairs
  }
}
