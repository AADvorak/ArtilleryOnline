import type {DeserializerInput} from "~/deserialization/deserializer-input";
import type {RoomConfig, VehicleConfig} from "~/playground/data/config";
import {DeserializerBase} from "~/deserialization/deserializer-base";
import {deserializeGunSpecs, deserializeJetSpecs} from "~/playground/data/specs-deserialize";

export function deserializeRoomConfig(input: DeserializerInput): RoomConfig {
  const background = DeserializerBase.readInt(input)
  const groundTexture = DeserializerBase.readInt(input)
  return {background, groundTexture}
}

export function deserializeVehicleConfig(input: DeserializerInput): VehicleConfig {
  const gun = deserializeGunSpecs(input)
  const jet = deserializeJetSpecs(input)
  const ammo = DeserializerBase.readMap(input, DeserializerBase.readString, DeserializerBase.readInt)!
  const missiles = DeserializerBase.readMap(input, DeserializerBase.readString, DeserializerBase.readInt)!
  const color = DeserializerBase.readNullable(input, DeserializerBase.readString)
  return {
    gun,
    jet,
    ammo,
    missiles,
    color
  }
}
