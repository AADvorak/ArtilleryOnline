<script setup lang="ts">
import { ref } from 'vue'
import { useBattleStore } from '~/stores/battle'
import {ApiRequestSender} from '~/api/api-request-sender'
import {useI18n} from "vue-i18n";

const {t} = useI18n()
const battleStore = useBattleStore()

const opened = ref(false)

async function leaveBattle() {
  hide()
  try {
    await new ApiRequestSender().delete('/battles/leave')
    battleStore.clear()
  } catch (e) {
    console.log(e)
  }
}

function show() {
  opened.value = true
}

function hide() {
  opened.value = false
}

defineExpose({
  show
})
</script>

<template>
  <v-dialog :model-value="opened" :persistent="true" max-width="600px">
    <v-card width="100%">
      <v-card-title>{{ t('leaveBattleDialog.title') }}</v-card-title>
      <v-card-text>
        <div class="d-flex">{{ t('leaveBattleDialog.message') }}</div>
        <div class="d-flex mt-4">
          <v-btn color="warning" @click="leaveBattle">{{ t('leaveBattleDialog.leave') }}</v-btn>
          <v-btn @click="hide">{{ t('common.cancel') }}</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
