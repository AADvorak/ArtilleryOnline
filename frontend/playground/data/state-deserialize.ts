import type {DeserializerInput} from "~/deserialization/deserializer-input";
import {DeserializerBase} from "~/deserialization/deserializer-base";
import {
  deserializeBodyPosition,
  deserializeBodyVelocity,
  deserializePosition,
  deserializeVelocity
} from "~/playground/data/common-deserialize";
import type {
  BattleModelState, DroneInVehicleState, DroneState,
  ExplosionState,
  GunState,
  JetState,
  MissileState,
  RoomState,
  ShellState,
  TrackState, VehicleState
} from "~/playground/data/state";
import type {MovingDirection} from "~/playground/data/common";

export function deserializeExplosionState(input: DeserializerInput): ExplosionState {
  const time = DeserializerBase.readDouble(input)
  const radius = DeserializerBase.readDouble(input)
  const position = deserializePosition(input)
  return {
    time,
    radius,
    position
  }
}

export function deserializeGunState(input: DeserializerInput): GunState {
  const loadedShell = DeserializerBase.readNullable(input, DeserializerBase.readString)
  const selectedShell = DeserializerBase.readNullable(input, DeserializerBase.readString)
  const loadingShell = DeserializerBase.readNullable(input, DeserializerBase.readString)
  const loadRemainTime = DeserializerBase.readDouble(input)
  const triggerPushed = DeserializerBase.readBoolean(input)
  return {
    loadedShell,
    selectedShell,
    loadingShell,
    loadRemainTime,
    triggerPushed
  }
}

export function deserializeJetState(input: DeserializerInput): JetState {
  const volume = DeserializerBase.readDouble(input)
  const active = DeserializerBase.readBoolean(input)
  return {
    volume,
    active
  }
}

export function deserializeMissileState(input: DeserializerInput): MissileState {
  const position = deserializeBodyPosition(input)
  const velocity = deserializeBodyVelocity(input)
  return {
    position,
    velocity
  }
}

export function deserializeRoomState(input: DeserializerInput): RoomState {
  const groundLine = DeserializerBase.readArray(input, DeserializerBase.readDouble)!
  return {groundLine}
}

export function deserializeShellState(input: DeserializerInput): ShellState {
  const position = deserializePosition(input)
  const velocity = deserializeVelocity(input)
  return {
    position,
    velocity
  }
}

export function deserializeTrackState(input: DeserializerInput): TrackState {
  const broken = DeserializerBase.readBoolean(input)
  const repairRemainTime = DeserializerBase.readDouble(input)
  return {
    broken,
    repairRemainTime
  }
}

export function deserializeDroneInVehicleState(input: DeserializerInput): DroneInVehicleState {
  const launched = DeserializerBase.readBoolean(input)
  const readyToLaunch = DeserializerBase.readBoolean(input)
  const prepareToLaunchRemainTime = DeserializerBase.readDouble(input)
  return {
    launched,
    readyToLaunch,
    prepareToLaunchRemainTime
  }
}

export function deserializeVehicleState(input: DeserializerInput): VehicleState {
  const position = deserializeBodyPosition(input)
  const velocity = deserializeBodyVelocity(input)
  const movingDirection = DeserializerBase.readNullable(input, DeserializerBase.readString) as MovingDirection
  const gunAngle = DeserializerBase.readDouble(input)
  const gunRotatingDirection = DeserializerBase.readNullable(input, DeserializerBase.readString) as MovingDirection
  const hitPoints = DeserializerBase.readDouble(input)
  const ammo = DeserializerBase.readMap(input, DeserializerBase.readString, DeserializerBase.readInt)!
  const missiles = DeserializerBase.readMap(input, DeserializerBase.readString, DeserializerBase.readInt)!
  const gunState = DeserializerBase.readNullable(input, deserializeGunState)!
  const trackState = DeserializerBase.readNullable(input, deserializeTrackState)!
  const jetState = DeserializerBase.readNullable(input, deserializeJetState)!
  const droneState = DeserializerBase.readNullable(input, deserializeDroneInVehicleState)!
  const onGround = DeserializerBase.readBoolean(input)
  return {
    position,
    velocity,
    movingDirection,
    gunAngle,
    gunRotatingDirection,
    hitPoints,
    ammo,
    missiles,
    gunState,
    trackState,
    jetState,
    droneState,
    onGround
  }
}

export function deserializeBattleModelState(input: DeserializerInput): BattleModelState {
  const vehicles = DeserializerBase.readMap(input, DeserializerBase.readString, deserializeVehicleState)
  const shells = DeserializerBase.readMap(input, DeserializerBase.readInt, deserializeShellState)
  const missiles = DeserializerBase.readMap(input, DeserializerBase.readInt, deserializeMissileState)
  const drones = DeserializerBase.readMap(input, DeserializerBase.readInt, deserializeDroneState)
  return {
    vehicles,
    shells,
    missiles,
    drones
  }
}

export function deserializeDroneState(input: DeserializerInput): DroneState {
  const position = deserializeBodyPosition(input)
  const velocity = deserializeBodyVelocity(input)
  const ammo = DeserializerBase.readMap(input, DeserializerBase.readString, DeserializerBase.readInt)!
  const gunState = deserializeGunState(input)
  const gunAngle = DeserializerBase.readDouble(input)
  return {
    position,
    velocity,
    ammo,
    gunState,
    gunAngle
  }
}
