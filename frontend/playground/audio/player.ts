import {useSoundsStore} from "~/stores/sounds";

export function usePlayer() {
  const audioCtx = new AudioContext()
  let buffer: AudioBuffer | undefined = undefined

  async function play(path: string) {
    await load(path)
    if (buffer) {
      const source = audioCtx.createBufferSource()
      source.buffer = buffer
      source.connect(audioCtx.destination)
      source.start()
    }
  }

  async function load(path: string) {
    const arrayBuffer = await useSoundsStore().loadSound(path)
    await decodeArrayBuffer(arrayBuffer)
  }

  async function decodeArrayBuffer(arrayBuffer: ArrayBuffer) {
    await audioCtx.decodeAudioData(arrayBuffer,
        data => buffer = data,
        error => console.log(error))
  }

  return {play}
}
