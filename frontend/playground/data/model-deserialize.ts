import type {DeserializerInput} from "~/deserialization/deserializer-input";
import type {
  ExplosionModel,
  MissileModel,
  RoomModel,
  ShellModel,
  VehicleModel,
  VehiclePreCalc
} from "~/playground/data/model";
import {DeserializerBase} from "~/deserialization/deserializer-base";
import {
  deserializeExplosionSpecs,
  deserializeMissileSpecs,
  deserializeRoomSpecs, deserializeShellSpecs, deserializeVehicleSpecs
} from "~/playground/data/specs-deserialize";
import {
  deserializeExplosionState,
  deserializeMissileState,
  deserializeRoomState, deserializeShellState, deserializeVehicleState
} from "~/playground/data/state-deserialize";
import {deserializeRoomConfig, deserializeVehicleConfig} from "~/playground/data/config-deserialize";

export function deserializeExplosionModel(input: DeserializerInput): ExplosionModel {
  const id = DeserializerBase.readInt(input)
  const specs = deserializeExplosionSpecs(input)
  const state = deserializeExplosionState(input)
  return {
    id,
    specs,
    state
  }
}

export function deserializeMissileModel(input: DeserializerInput): MissileModel {
  const id = DeserializerBase.readInt(input)
  const vehicleId = DeserializerBase.readInt(input)
  const specs = deserializeMissileSpecs(input)
  const state = deserializeMissileState(input)
  return {
    id,
    vehicleId,
    specs,
    state
  }
}

export function deserializeRoomModel(input: DeserializerInput): RoomModel {
  const specs = deserializeRoomSpecs(input)
  const config = deserializeRoomConfig(input)
  const state = deserializeRoomState(input)
  return {
    specs,
    config,
    state
  }
}

export function deserializeShellModel(input: DeserializerInput): ShellModel {
  const id = DeserializerBase.readInt(input)
  const specs = deserializeShellSpecs(input)
  const state = deserializeShellState(input)
  return {
    id,
    specs,
    state
  }
}

export function deserializeVehiclePreCalc(input: DeserializerInput): VehiclePreCalc {
  const wheelDistance = DeserializerBase.readDouble(input)
  const wheelAngle = DeserializerBase.readDouble(input)
  const mass = DeserializerBase.readDouble(input)
  return {
    wheelDistance,
    wheelAngle,
    mass
  }
}

export function deserializeVehicleModel(input: DeserializerInput): VehicleModel {
  const id = DeserializerBase.readInt(input)
  const specs = deserializeVehicleSpecs(input)
  const preCalc = deserializeVehiclePreCalc(input)
  const config = deserializeVehicleConfig(input)
  const state = deserializeVehicleState(input)
  return {
    id,
    specs,
    preCalc,
    config,
    state
  }
}
