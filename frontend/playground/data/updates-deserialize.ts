import type {
  BattleModelAdded,
  BattleModelRemoved,
  BattleModelUpdates,
  RoomStateUpdate
} from "~/playground/data/updates";
import {DeserializerBase} from "~/deserialization/deserializer-base";
import type {DeserializerInput} from "~/deserialization/deserializer-input";
import {
  deserializeDroneModel,
  deserializeExplosionModel,
  deserializeMissileModel,
  deserializeShellModel
} from "~/playground/data/model-deserialize";

export function deserializeBattleModelAdded(input: DeserializerInput): BattleModelAdded {
  const shells = DeserializerBase.readArray(input, deserializeShellModel)
  const explosions = DeserializerBase.readArray(input, deserializeExplosionModel)
  const missiles = DeserializerBase.readArray(input, deserializeMissileModel)
  const drones = DeserializerBase.readArray(input, deserializeDroneModel)
  return {
    shells,
    explosions,
    missiles,
    drones
  }
}

export function deserializeBattleModelRemoved(input: DeserializerInput): BattleModelRemoved {
  const shellIds = DeserializerBase.readArray(input, DeserializerBase.readInt)
  const explosionIds = DeserializerBase.readArray(input, DeserializerBase.readInt)
  const missileIds = DeserializerBase.readArray(input, DeserializerBase.readInt)
  const droneIds = DeserializerBase.readArray(input, DeserializerBase.readInt)
  const vehicleKeys = DeserializerBase.readArray(input, DeserializerBase.readString)
  return {
    shellIds,
    explosionIds,
    missileIds,
    droneIds,
    vehicleKeys
  }
}

export function deserializeRoomStateUpdate(input: DeserializerInput): RoomStateUpdate {
  const begin = DeserializerBase.readInt(input)
  const groundLinePart = DeserializerBase.readArray(input, DeserializerBase.readDouble)!
  return {
    begin,
    groundLinePart
  }
}

export function deserializeBattleModelUpdates(input: DeserializerInput): BattleModelUpdates {
  const added = DeserializerBase.readNullable(input, deserializeBattleModelAdded)
  const removed = DeserializerBase.readNullable(input, deserializeBattleModelRemoved)
  const roomStateUpdates = DeserializerBase.readArray(input, deserializeRoomStateUpdate)
  return {
    added,
    removed,
    roomStateUpdates
  }
}
