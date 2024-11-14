import {useSoundsStore} from "~/stores/sounds";

export interface Player {
  play: (path: string, pan: number, loop?: boolean) => Promise<AudioControl | undefined>
}

export interface AudioControl {
  audioCtx: AudioContext
  source: AudioBufferSourceNode
  panner: StereoPannerNode
  gainNode: GainNode
}

// todo read from metadata
const LOOPS = {
  '/sounds/gun-turn.wav': {
    start: 0.433,
    end: 0.912
  }
}

export function usePlayer(): Player {
  const audioCtx = new AudioContext()
  const soundsStore = useSoundsStore()

  async function play(path: string, pan: number, loop: boolean = false): Promise<AudioControl | undefined> {
    let buffer = soundsStore.audioBufferMap[path]
    if (!buffer) {
      buffer = await load(path) as AudioBuffer
      soundsStore.audioBufferMap[path] = buffer
    }
    if (buffer) {
      const panner = new StereoPannerNode(audioCtx, {pan})
      const gainNode = audioCtx.createGain();
      const source = audioCtx.createBufferSource()
      source.buffer = buffer
      if (loop) {
        setLoop(source, path)
      }
      source.connect(panner).connect(gainNode).connect(audioCtx.destination)
      source.start()
      return {audioCtx, source, panner, gainNode}
    }
    return undefined
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

  function setLoop(source: AudioBufferSourceNode, path: string) {
    source.loop = true
    const loopRange = LOOPS[path]
    if (loopRange) {
      source.loopStart = loopRange.start
      source.loopEnd = loopRange.end
    }
  }

  return {play}
}
