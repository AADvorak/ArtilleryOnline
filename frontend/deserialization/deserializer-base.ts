import type {DeserializerInput} from "~/deserialization/deserializer-input";

export const DeserializerBase = {
  readBoolean(input: DeserializerInput) {
    const value = input.dataView.getInt8(input.offset)
    input.offset++
    return value > 0
  },

  readInt(input: DeserializerInput) {
    const value = input.dataView.getInt32(input.offset)
    input.offset += 4
    return value
  },

  readLong(input: DeserializerInput) {
    const value = Number(input.dataView.getBigInt64(input.offset))
    input.offset += 8
    return value
  },

  readFloat(input: DeserializerInput) {
    const value = input.dataView.getFloat32(input.offset)
    input.offset += 4
    return value
  },

  readDouble(input: DeserializerInput) {
    const value = input.dataView.getFloat64(input.offset)
    input.offset += 8
    return value
  },

  readString(input: DeserializerInput) {
    const length = this.readInt(input)
    const bytes = new Uint8Array(input.data, input.offset, length)
    input.offset += length
    return new TextDecoder().decode(bytes)
  },

  readNullable<V>(input: DeserializerInput, valueReader: (i: DeserializerInput) => V) {
    if (DeserializerBase.readBoolean(input)) {
      return valueReader(input)
    }
    return undefined
  },

  readArray<V>(input: DeserializerInput, valueReader: (i: DeserializerInput) => V) {
    let array = null
    if (DeserializerBase.readBoolean(input)) {
      array = []
      const length = DeserializerBase.readInt(input)
      for (let i = 0; i < length; i++) {
        array.push(valueReader(input))
      }
    }
    return array
  },

  readMap<K, V>(input: DeserializerInput, keyReader: (i: DeserializerInput) => K, valueReader: (i: DeserializerInput) => V) {
    let map = null
    if (DeserializerBase.readBoolean(input)) {
      map = {}
      const length = DeserializerBase.readInt(input)
      for (let i = 0; i < length; i++) {
        // @ts-ignore
        map[String(keyReader(input))] = valueReader(input)
      }
    }
    return map
  }
}
