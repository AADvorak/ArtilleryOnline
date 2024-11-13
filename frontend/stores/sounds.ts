import {ref} from 'vue'
import {defineStore} from 'pinia'

export interface AudioBufferMap {
  [path: string]: AudioBuffer
}

export const useSoundsStore = defineStore('sounds', () => {
  const audioBufferMap = ref<AudioBufferMap>({})
  return {audioBufferMap}
})
