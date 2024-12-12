<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useBattleStore } from '~/stores/battle'
import { BattleStage } from '@/playground/data/battle'
import {useRouter} from "#app";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
const battleStore = useBattleStore()
const router = useRouter()

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
  setTimeout(() => router.push('/'))
}
</script>

<template>
  <v-dialog :model-value="opened" :persistent="true" max-width="600px">
    <v-card width="100%">
      <v-card-title>{{ t('finishBattleDialog.title') }}</v-card-title>
      <v-card-text>
        <div class="d-flex">{{ t('finishBattleDialog.message') }}</div>
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hideAndCleanBattle">{{ t('common.ok') }}</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
