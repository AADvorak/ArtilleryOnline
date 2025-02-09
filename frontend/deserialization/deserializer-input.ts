export class DeserializerInput {
  data: ArrayBuffer
  dataView: DataView
  offset: number

  constructor(data: ArrayBuffer) {
    this.data = data
    this.dataView = new DataView(data)
    this.offset = 0
  }
}
