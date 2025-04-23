<script setup lang="ts">
import {ref} from "vue";
import { mdiSword } from '@mdi/js'
import {useI18n} from "vue-i18n";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRouter} from "#app";
import {useBattleStore} from "~/stores/battle";
import LeaveBattleDialog from "~/playground/components/LeaveBattleDialog.vue";

const api = new ApiRequestSender()
const {t} = useI18n()
const battleStore = useBattleStore()
const router = useRouter()

const leaveBattleDialog = ref<InstanceType<typeof LeaveBattleDialog> | undefined>()

const opened = ref(false)

const isShow = computed(() => {
  return !!battleStore.battle
})

async function toBattle() {
  await router.push('/playground')
}

function showLeaveBattleDialog() {
  leaveBattleDialog.value?.show()
}
</script>

<template>
  <v-menu
      v-if="isShow"
      v-model="opened"
      close-on-content-click
      location="bottom"
  >
    <template v-slot:activator="{ props }">
      <v-btn
          class="btn-with-icon"
          color="error"
          v-bind="props"
      >
        <v-icon class="icon-large" :icon="mdiSword" />
        <v-tooltip
            activator="parent"
            location="bottom"
            open-delay="1000">
          {{ t('menu.battle') }}
        </v-tooltip>
      </v-btn>
    </template>
    <v-card>
      <v-btn class="mt-2" width="100%" @click="showLeaveBattleDialog">{{ t('common.exit') }}</v-btn>
      <v-btn class="mt-2" width="100%" color="error" @click="toBattle">
        {{ t('menu.backToBattle') }}
      </v-btn>
    </v-card>
  </v-menu>
  <LeaveBattleDialog ref="leaveBattleDialog"/>
</template>

<style scoped>
.btn-with-icon {
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
