<script setup lang="ts">
import {useRoute, useRouter} from '#app';
import {computed} from 'vue';
import {useI18n} from "vue-i18n";
import {mdiChevronLeft} from "@mdi/js";
import IconBtn from "~/components/icon-btn.vue";

const route = useRoute()
const router = useRouter()
const {t} = useI18n()

const pathComponents = computed(() => {
  const path = route.path
  if (path === '/') {
    return []
  }
  return path.split('/').filter(component => component !== '')
})

const pathSegments = computed(() => {
  const segments = []
  let currentPath = ''
  
  for (let i = 0; i < pathComponents.value.length; i++) {
    currentPath += `/${pathComponents.value[i]}`
    segments.push({
      text: pathComponents.value[i]!,
      path: currentPath
    })
  }
  
  return segments
})

async function navigateTo(path: string) {
  await router.push(path)
}

function back() {
  router.back()
}
</script>

<template>
  <div class="menu-navigation">
    <icon-btn
        :icon="mdiChevronLeft"
        :tooltip="t('navigation.back')"
        @click="back"
    />
    <span
        class="navigation-link"
        @click="navigateTo('/')"
    >
        Artillery online
    </span>
    <span>:&nbsp;</span>
    <template v-for="(segment, index) in pathSegments">
      <span
          :class="index < pathSegments.length - 1 ? 'navigation-link' : ''"
          @click="navigateTo(segment.path)"
      >
        {{ t(`navigation.${segment.text}`) }}
      </span>
      <span v-if="index < pathSegments.length - 1" class="separator"> / </span>
    </template>
  </div>
</template>

<style scoped>
.menu-navigation {
  display: inline-flex;
  align-items: center;
  flex-wrap: wrap;
}

.navigation-link {
  cursor: pointer;
  color: inherit;
  text-decoration: underline;
}

.separator {
  margin: 0 4px;
  color: inherit;
}
</style>
