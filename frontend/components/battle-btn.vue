<script setup lang="ts">
import {ref} from "vue";
import { mdiSword } from '@mdi/js'
import {useI18n} from "vue-i18n";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRouter} from "#app";
import {useBattleStore} from "~/stores/battle";

const api = new ApiRequestSender()
const {t} = useI18n()
const battleStore = useBattleStore()
const router = useRouter()

const opened = ref(false)

const isShow = computed(() => {
  return !!battleStore.battle
})

async function toBattle() {
  await router.push('/playground')
}
</script>

<template>
  <v-btn
      v-if="isShow"
      class="battle-btn"
      color="error"
      @click="toBattle"
  >
    <v-icon class="icon-large" :icon="mdiSword" />
    <v-tooltip
        activator="parent"
        location="bottom"
        open-delay="1000">
      {{ t('menu.backToBattle') }}
    </v-tooltip>
  </v-btn>
</template>

<style scoped>
.battle-btn {
  width: 48px;
  min-width: 48px;
  padding: 0 0;
  text-align: center;
  position: absolute;
  left: 50%;
  top: 25px;
  transform: translate(-50%, -50%);
}

.icon-large {
  width: 28px;
  height: 28px;
}
</style>
