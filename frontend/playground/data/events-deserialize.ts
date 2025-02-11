import type {
  BattleModelEvents,
  RicochetEvent,
  ShellHitEvent,
  ShellHitEventObject,
  VehicleCollideEvent
} from "~/playground/data/events";
import type {DeserializerInput} from "~/deserialization/deserializer-input";
import {DeserializerBase} from "~/deserialization/deserializer-base";
import {type CollideObject, CollideObjectType, type ShellHitType} from "~/playground/data/common";

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
  const vehicleId = DeserializerBase.readNullable(input, DeserializerBase.readInt)
  const type = DeserializerBase.readString(input) as CollideObjectType
  return {
    vehicleId,
    type
  }
}

export function deserializeVehicleCollideEvent(input: DeserializerInput): VehicleCollideEvent {
  const object = deserializeCollideObject(input)
  const vehicleId = DeserializerBase.readInt(input)
  return {
    object,
    vehicleId
  }
}

export function deserializeRicochetEvent(input: DeserializerInput): RicochetEvent {
  const shellId = DeserializerBase.readInt(input)
  return {
    shellId
  }
}

export function deserializeBattleModelEvents(input: DeserializerInput): BattleModelEvents {
  const hits = DeserializerBase.readArray(input, deserializeShellHitEvent)
  const collides = DeserializerBase.readArray(input, deserializeVehicleCollideEvent)
  const ricochets = DeserializerBase.readArray(input, deserializeRicochetEvent)
  return {
    hits,
    collides,
    ricochets
  }
}
