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

</script>

<template>
  <div class="battle-fps">{{ fps }}fps</div>
</template>

<style scoped>
.battle-fps {
  color: greenyellow;
  font-size: large;
}
</style>
