<script setup lang="ts">
import {mdiFullscreen, mdiFullscreenExit} from "@mdi/js";
import IconBtn from "~/components/icon-btn.vue";
import {useI18n} from "vue-i18n";
import {useErrorsStore} from "~/stores/errors";

const {t} = useI18n()

const isFullScreen = ref<boolean>(false)

onMounted(() => {
  addEventListener('fullscreenchange', checkFullScreen)
  checkFullScreen()
})

onUnmounted(() => {
  removeEventListener('fullscreenchange', checkFullScreen)
})

async function toggleFullScreen() {
  try {
    if (!document.fullscreenElement) {
      await document.documentElement.requestFullscreen()
    } else if (document.exitFullscreen) {
      await document.exitFullscreen()
    }
  } catch (e) {
    useErrorsStore().messages.push(t('fullScreenBtn.error'))
  }
}

function checkFullScreen() {
  isFullScreen.value = !!document.fullscreenElement
}
</script>

<template>
  <icon-btn
      :icon="isFullScreen ? mdiFullscreenExit : mdiFullscreen"
      :tooltip="isFullScreen ? t('fullScreenBtn.exit') : t('fullScreenBtn.enter')"
      @click="toggleFullScreen"
  />
</template>
