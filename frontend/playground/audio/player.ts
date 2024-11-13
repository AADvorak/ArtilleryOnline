import {useSoundsStore} from "~/stores/sounds";

export interface Player {
  play: (path: string, pan: number) => void
}

export function usePlayer(): Player {
  const audioCtx = new AudioContext()
  const soundsStore = useSoundsStore()

  async function play(path: string, pan: number) {
    let buffer = soundsStore.audioBufferMap[path]
    if (!buffer) {
      buffer = await load(path) as AudioBuffer
      soundsStore.audioBufferMap[path] = buffer
    }
    if (buffer) {
      const panner = new StereoPannerNode(audioCtx, {pan})
      const source = audioCtx.createBufferSource()
      source.buffer = buffer
      source.connect(panner).connect(audioCtx.destination)
      source.start()
    }
  }

  async function load(path: string) {
    const response = await fetch(path)
    const arrayBuffer = await response.arrayBuffer()
    return await decodeArrayBuffer(arrayBuffer)
  }

  function decodeArrayBuffer(arrayBuffer: ArrayBuffer) {
    return new Promise((resolve, reject) => {
      audioCtx.decodeAudioData(arrayBuffer,
          data => resolve(data),
          error => reject(error)).then()
    })
  }

  return {play}
}
