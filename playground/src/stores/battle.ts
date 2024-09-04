import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import type {Battle} from "@/data/battle";

export const useBattleStore = defineStore('battle', () => {
  const battle = ref<Battle>()

  const vehicles = computed(() => battle.value?.model?.vehicles)

  return { battle, vehicles }
})
