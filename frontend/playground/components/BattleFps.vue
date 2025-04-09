<script setup lang="ts">
import {useBattleStore} from "~/stores/battle";
import {computed} from "vue";

const battleStore = useBattleStore()

const fps = computed(() => {
  const battle = battleStore.battle
  if (!battle) {
    return 0
  }
  return battle.fps
})

const fpsClass = computed(() => {
  if (fps.value > 70) {
    return 'battle-fps-green'
  } else if (fps.value > 40) {
    return 'battle-fps-orange'
  } else {
    return 'battle-fps-red'
  }
})
</script>

<template>
  <div :class="fpsClass">{{ fps }} fps</div>
</template>

<style scoped>
.battle-fps-green {
  color: green;
  font-size: 16px;
}

.battle-fps-orange {
  color: orange;
  font-size: 16px;
}

.battle-fps-red {
  color: crimson;
  font-size: 16px;
}
</style>
