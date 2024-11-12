import {ref} from 'vue'
import {defineStore} from 'pinia'

interface SoundsMap {
  [path: string]: Blob
}

export const useSoundsStore = defineStore('sounds', () => {
  const sounds = ref<SoundsMap>({})

  async function loadSound(path: string) {
    if (!sounds.value[path]) {
      try {
        const response = await fetch(path)
        sounds.value[path] = await response.blob()
      } catch (e) {
        console.log(e)
      }
    }
    return await sounds.value[path].arrayBuffer()
  }

  return {loadSound}
})
