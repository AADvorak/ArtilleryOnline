<script setup lang="ts">
import {computed, ref, watch} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {BattleStage} from '@/playground/data/battle'
import {useRouter} from "#app";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
const battleStore = useBattleStore()
const router = useRouter()

const battle = computed(() => battleStore.battle)

const title = computed(() => {
  const battleStage = battle.value?.battleStage
  if (battleStage === BattleStage.FINISHED) {
    return t('finishBattleDialog.title')
  }
  if (battleStage === BattleStage.ERROR) {
    return t('common.error')
  }
  return ''
})

const message = computed(() => {
  const battleStage = battle.value?.battleStage
  if (battleStage === BattleStage.FINISHED) {
    return t('finishBattleDialog.message')
  }
  if (battleStage === BattleStage.ERROR) {
    return t('finishBattleDialog.errorMessage')
  }
  return ''
})

const opened = ref(false)

watch(battle, (value) => {
  const battleStage = value?.battleStage
  if ([BattleStage.FINISHED, BattleStage.ERROR].includes(battleStage)) {
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
      <v-card-title>{{ title }}</v-card-title>
      <v-card-text>
        <div class="d-flex">{{ message }}</div>
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hideAndCleanBattle">{{ t('common.ok') }}</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
