import type {DeserializerInput} from "~/deserialization/deserializer-input";
import {DeserializerBase} from "~/deserialization/deserializer-base";
import {type HalfCircleShape, type Shape, ShapeNames, type TrapezeShape} from "~/playground/data/shapes";

export function deserializeShape(input: DeserializerInput): Shape | HalfCircleShape | TrapezeShape {
  const name = DeserializerBase.readString(input)
  switch (name) {
    case ShapeNames.HALF_CIRCLE:
      const radius = DeserializerBase.readDouble(input)
      return {name, radius}
    case ShapeNames.TRAPEZE:
      const bottomRadius = DeserializerBase.readDouble(input)
      const topRadius = DeserializerBase.readDouble(input)
      const height = DeserializerBase.readDouble(input)
      return {name, bottomRadius, topRadius, height}
    default:
      return {name}
  }
}
