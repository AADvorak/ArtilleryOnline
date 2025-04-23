<script setup lang="ts">
import {ref} from "vue";
import { mdiSwordCross } from '@mdi/js'
import {useI18n} from "vue-i18n";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRouter} from "#app";
import {useBattleStore} from "~/stores/battle";

const api = new ApiRequestSender()
const {t} = useI18n()
const battleStore = useBattleStore()
const router = useRouter()

const mounted = ref(false)

const largeScale = ref(false)

const isShow = computed(() => {
  return !!battleStore.battle
})

const iconClass = computed(() => {
  return `icon ${largeScale.value ? 'icon-large-scale' : 'icon-normal-scale'}`
})

onMounted(() => {
  mounted.value = true
  startAnimation()
})

onUnmounted(() => {
  mounted.value = false
})

async function toBattle() {
  await router.push('/playground')
}

function startAnimation() {
  if (!isShow.value || !mounted.value) {
    return
  }
  largeScale.value = !largeScale.value
  setTimeout(startAnimation, 500)
}
</script>

<template>
  <v-btn
      v-if="isShow"
      class="battle-btn"
      color="error"
      @click="toBattle"
  >
    <v-icon :class="iconClass" :icon="mdiSwordCross" />
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

.icon {
  width: 28px;
  height: 28px;
  transition: transform 0.5s ease;
}

.icon-large-scale {
  transform: scale(1.4);
}

.icon-normal-scale {
  transform: scale(1);
}
</style>
