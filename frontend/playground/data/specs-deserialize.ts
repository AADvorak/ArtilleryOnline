import type {DeserializerInput} from "~/deserialization/deserializer-input";
import {DeserializerBase} from "~/deserialization/deserializer-base";
import type {
  DroneSpecs,
  ExplosionSpecs,
  GunSpecs,
  JetSpecs,
  MissileSpecs,
  RoomSpecs,
  ShellSpecs,
  VehicleSpecs
} from "~/playground/data/specs";
import {JetType, type ShellType} from "~/playground/data/common";
import {deserializePosition} from "~/playground/data/common-deserialize";
import {deserializeShape} from "~/playground/data/shapes-deserialize";

export function deserializeExplosionSpecs(input: DeserializerInput): ExplosionSpecs {
  const duration = DeserializerBase.readDouble(input)
  const radius = DeserializerBase.readDouble(input)
  return {duration, radius}
}

export function deserializeGunSpecs(input: DeserializerInput): GunSpecs {
  const ammo = DeserializerBase.readInt(input)
  const loadTime = DeserializerBase.readDouble(input)
  const rotationVelocity = DeserializerBase.readDouble(input)
  const length = DeserializerBase.readDouble(input)
  const caliber = DeserializerBase.readDouble(input)
  const availableShells = DeserializerBase.readMap(input, DeserializerBase.readString, deserializeShellSpecs)!
  return {
    ammo,
    loadTime,
    rotationVelocity,
    length,
    caliber,
    availableShells
  }
}

export function deserializeJetSpecs(input: DeserializerInput): JetSpecs {
  const capacity = DeserializerBase.readDouble(input)
  const consumption = DeserializerBase.readDouble(input)
  const regeneration = DeserializerBase.readDouble(input)
  const acceleration = DeserializerBase.readDouble(input)
  const type = DeserializerBase.readString(input) as JetType
  return {
    capacity,
    consumption,
    regeneration,
    acceleration,
    type
  }
}

export function deserializeMissileSpecs(input: DeserializerInput): MissileSpecs {
  const pushingAcceleration = DeserializerBase.readDouble(input)
  const correctingAccelerationCoefficient = DeserializerBase.readDouble(input)
  const minCorrectingVelocity = DeserializerBase.readDouble(input)
  const anglePrecision = DeserializerBase.readDouble(input)
  const damage = DeserializerBase.readDouble(input)
  const radius = DeserializerBase.readDouble(input)
  const mass = DeserializerBase.readDouble(input)
  const caliber = DeserializerBase.readDouble(input)
  const length = DeserializerBase.readDouble(input)
  return {
    pushingAcceleration,
    correctingAccelerationCoefficient,
    minCorrectingVelocity,
    anglePrecision,
    damage,
    radius,
    mass,
    caliber,
    length
  }
}

export function deserializeRoomSpecs(input: DeserializerInput): RoomSpecs {
  const leftBottom = deserializePosition(input)
  const rightTop = deserializePosition(input)
  const step = DeserializerBase.readDouble(input)
  const gravityAcceleration = DeserializerBase.readDouble(input)
  const groundReactionCoefficient = DeserializerBase.readDouble(input)
  const groundFrictionCoefficient = DeserializerBase.readDouble(input)
  const airFrictionCoefficient = DeserializerBase.readDouble(input)
  const groundMaxDepth = DeserializerBase.readDouble(input)
  return {
    leftBottom,
    rightTop,
    step,
    gravityAcceleration,
    groundReactionCoefficient,
    groundFrictionCoefficient,
    airFrictionCoefficient,
    groundMaxDepth
  }
}

export function deserializeShellSpecs(input: DeserializerInput): ShellSpecs {
  const velocity = DeserializerBase.readDouble(input)
  const damage = DeserializerBase.readDouble(input)
  const radius = DeserializerBase.readDouble(input)
  const mass = DeserializerBase.readDouble(input)
  const caliber = DeserializerBase.readDouble(input)
  const type = DeserializerBase.readString(input) as ShellType
  return {
    velocity,
    damage,
    radius,
    mass,
    caliber,
    type
  }
}

export function deserializeVehicleSpecs(input: DeserializerInput): VehicleSpecs {
  const name = DeserializerBase.readString(input)
  const hitPoints = DeserializerBase.readDouble(input)
  const missiles = DeserializerBase.readInt(input)
  const minAngle = DeserializerBase.readDouble(input)
  const maxAngle = DeserializerBase.readDouble(input)
  const acceleration = DeserializerBase.readDouble(input)
  const wheelAngleVelocity = DeserializerBase.readDouble(input)
  const radius = DeserializerBase.readDouble(input)
  const turretShape = deserializeShape(input)
  const wheelRadius = DeserializerBase.readDouble(input)
  const hullRadius = DeserializerBase.readDouble(input)
  const trackRepairTime = DeserializerBase.readDouble(input)
  const minTrackHitCaliber = DeserializerBase.readDouble(input)
  const availableGuns = DeserializerBase.readMap(input, DeserializerBase.readString, deserializeGunSpecs)!
  const availableJets = DeserializerBase.readMap(input, DeserializerBase.readString, deserializeJetSpecs)!
  const availableMissiles = DeserializerBase.readMap(input, DeserializerBase.readString, deserializeMissileSpecs)!
  const availableDrones = DeserializerBase.readMap(input, DeserializerBase.readString, deserializeDroneSpecs)!
  const defaultGun = DeserializerBase.readString(input)
  return {
    name,
    hitPoints,
    missiles,
    minAngle,
    maxAngle,
    acceleration,
    wheelAngleVelocity,
    radius,
    turretShape,
    wheelRadius,
    hullRadius,
    trackRepairTime,
    minTrackHitCaliber,
    availableGuns,
    availableJets,
    availableMissiles,
    availableDrones,
    defaultGun
  }
}

export function deserializeDroneSpecs(input: DeserializerInput): DroneSpecs {
  const maxEngineAcceleration = DeserializerBase.readDouble(input)
  const hullRadius = DeserializerBase.readDouble(input)
  const enginesRadius = DeserializerBase.readDouble(input)
  const mass = DeserializerBase.readDouble(input)
  const flyHeight = DeserializerBase.readDouble(input)
  const criticalAngle = DeserializerBase.readDouble(input)
  const prepareToLaunchTime = DeserializerBase.readDouble(input)
  const availableGuns = DeserializerBase.readMap(input, DeserializerBase.readString, deserializeGunSpecs)!
  return {
    maxEngineAcceleration,
    hullRadius,
    enginesRadius,
    mass,
    flyHeight,
    criticalAngle,
    prepareToLaunchTime,
    availableGuns
  }
}
