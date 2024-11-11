export function usePlayer() {
  const audioCtx = new AudioContext()
  let buffer = undefined

  async function play(path) {
    await load(path)
    if (buffer) {
      const source = audioCtx.createBufferSource()
      source.buffer = buffer
      source.connect(audioCtx.destination)
      source.start()
    }
  }

  async function load(path) {
    const response = await fetch(path)
    const arrayBuffer = await response.arrayBuffer()
    await decodeArrayBuffer(arrayBuffer)
  }

  async function decodeArrayBuffer(arrayBuffer) {
    await audioCtx.decodeAudioData(arrayBuffer,
        data => buffer = data,
        error => console.log(error))
  }

  return {play}
}
