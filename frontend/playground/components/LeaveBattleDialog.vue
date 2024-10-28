<script setup lang="ts">
import { ref } from 'vue'
import { useBattleStore } from '~/stores/battle'
import {useRouter} from '#app'
import {ApiRequestSender} from '~/api/api-request-sender'

const battleStore = useBattleStore()
const router = useRouter()

const opened = ref(false)

async function leaveBattle() {
  hide()
  try {
    await new ApiRequestSender().delete('/battles/leave')
    battleStore.clear()
    await router.push('/')
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
      <v-card-title>Leave battle</v-card-title>
      <v-card-text>
        <div class="d-flex">Are you sure you want to leave battle? There will be no possibility to return.</div>
        <div class="d-flex mt-4">
          <v-btn color="warning" @click="leaveBattle">Leave</v-btn>
          <v-btn @click="hide">Cancel</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
