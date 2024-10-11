<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useBattleStore } from '~/stores/battle'
import { BattleStage } from '@/playground/data/battle'

const battleStore = useBattleStore()

const battle = computed(() => battleStore.battle)

const opened = ref(false)

watch(battle, (value) => {
  const battleStage = value?.battleStage
  if (battleStage === BattleStage.FINISHED) {
    opened.value = true
  }
})

function hideAndCleanBattle() {
  opened.value = false
  battleStore.clear()
}
</script>

<template>
  <v-dialog :model-value="opened" :persistent="true" max-width="600px">
    <v-card width="100%">
      <v-card-title>End of the battle</v-card-title>
      <v-card-text>
        <div class="d-flex">The battle has finished</div>
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hideAndCleanBattle">OK</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
