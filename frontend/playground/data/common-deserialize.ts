import type {DeserializerInput} from "~/deserialization/deserializer-input";
import type {
  Ammo,
  BodyPosition,
  BodyVector,
  BodyVelocity,
  Missiles,
  Position, Shift,
  Vector,
  Velocity
} from "~/playground/data/common";
import {DeserializerBase} from "~/deserialization/deserializer-base";

export function deserializeVector(input: DeserializerInput): Vector {
  const x = DeserializerBase.readDouble(input)
  const y = DeserializerBase.readDouble(input)
  return {x, y}
}

export function deserializeBodyVector(input: DeserializerInput): BodyVector {
  const {x, y} = deserializeVector(input)
  const angle = DeserializerBase.readDouble(input)
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

export function deserializeAmmo(input: DeserializerInput): Ammo | undefined {
  return DeserializerBase.readMap(input, DeserializerBase.readString, DeserializerBase.readInt)
}

export function deserializeMissiles(input: DeserializerInput): Missiles | undefined {
  return DeserializerBase.readMap(input, DeserializerBase.readString, DeserializerBase.readInt)
}

export function deserializeShift(input: DeserializerInput): Shift {
  const distance = DeserializerBase.readDouble(input)
  const angle = DeserializerBase.readDouble(input)
  return {distance, angle}
}
