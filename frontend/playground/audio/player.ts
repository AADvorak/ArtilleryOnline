import {useSoundsStore} from "~/stores/sounds";

export interface PlayParams {
  path: string
  pan: number
  gain: number
  rate?: number
  loop?: boolean
  randomise?: boolean
}

export interface Player {
  play: (params: PlayParams) => Promise<AudioControl | undefined>
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
  },
  '/sounds/jet.wav': {
    start: 0.497
  }
}

export function usePlayer(): Player {
  const audioCtx = new AudioContext()
  const soundsStore = useSoundsStore()

  async function play(params: PlayParams): Promise<AudioControl | undefined> {
    let buffer = soundsStore.audioBufferMap[params.path]
    if (!buffer) {
      buffer = await load(params.path) as AudioBuffer
      soundsStore.audioBufferMap[params.path] = buffer
    }
    if (buffer) {
      const panner = new StereoPannerNode(audioCtx, {pan: params.pan})
      const gainNode = audioCtx.createGain()
      gainNode.gain.value = params.gain
      const source = audioCtx.createBufferSource()
      source.buffer = buffer
      if (params.loop) {
        setLoop(source, params.path)
      }
      if (params.rate) {
        source.playbackRate.value = params.rate
      }
      if (params.randomise) {
        source.playbackRate.value += Math.random() * 0.3 - 0.15
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
    return await audioCtx.decodeAudioData(arrayBuffer)
  }

  function setLoop(source: AudioBufferSourceNode, path: string) {
    source.loop = true
    const loopRange = LOOPS[path]
    if (loopRange) {
      if (loopRange.start) {
        source.loopStart = loopRange.start
      }
      if (loopRange.end) {
        source.loopEnd = loopRange.end
      }
    }
  }

  return {play}
}
