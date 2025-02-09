import type {DeserializerInput} from "~/deserialization/deserializer-input";
import type {
  Ammo,
  BodyPosition,
  BodyVector,
  BodyVelocity,
  Missiles,
  Position,
  Vector,
  Velocity
} from "~/playground/data/common";
import {DeserializerBase} from "~/deserialization/deserializer-base";

export function deserializeVector(input: DeserializerInput): Vector {
  const x = DeserializerBase.readLong(input)
  const y = DeserializerBase.readLong(input)
  return {x, y}
}

export function deserializeBodyVector(input: DeserializerInput): BodyVector {
  const {x, y} = deserializeVector(input)
  const angle = DeserializerBase.readLong(input)
  return {x, y, angle}
}

export function deserializePosition(input: DeserializerInput): Position {
  return deserializeVector(input)
}

export function deserializeBodyPosition(input: DeserializerInput): BodyPosition {
  return deserializeBodyVector(input)
}

export function deserializeVelocity(input: DeserializerInput): Velocity {
  return deserializeVector(input)
}

export function deserializeBodyVelocity(input: DeserializerInput): BodyVelocity {
  return deserializeBodyVector(input)
}

export function deserializeAmmo(input: DeserializerInput): Ammo | null {
  return DeserializerBase.readMap(input, DeserializerBase.readString, DeserializerBase.readInt)
}

export function deserializeMissiles(input: DeserializerInput): Missiles | null {
  return DeserializerBase.readMap(input, DeserializerBase.readString, DeserializerBase.readInt)
}
