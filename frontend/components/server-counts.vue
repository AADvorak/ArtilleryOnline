<script setup lang="ts">
import {ApiRequestSender} from "~/api/api-request-sender";
import {mdiAccountMultiple, mdiSwordCross} from "@mdi/js";
import {useI18n} from "vue-i18n";

interface CountsResponse {
  battles: number
  onlineUsers: number
}

const {t} = useI18n()

const api = new ApiRequestSender()

const counts = ref<CountsResponse | undefined>()

const onlineUsersTooltip = computed(() => {
  if (!counts.value) {
    return ''
  }
  const count = counts.value.onlineUsers
  if (count === 0) {
    return t('serverCounts.onlineUsers0')
  } else if (count === 1) {
    return t('serverCounts.onlineUsers1')
  } else if (count === 2) {
    return t('serverCounts.onlineUsers2')
  } else if (count >= 3 && count <= 10) {
    return t('serverCounts.onlineUsers3to10')
  } else {
    return t('serverCounts.onlineUsers')
  }
})

const battlesTooltip = computed(() => {
  if (!counts.value) {
    return ''
  }
  const count = counts.value.battles
  if (count === 0) {
    return t('serverCounts.battles0')
  } else if (count === 1) {
    return t('serverCounts.battles1')
  } else if (count >= 2 && count <= 10) {
    return t('serverCounts.battles2to10')
  } else {
    return t('serverCounts.battles')
  }
})

onMounted(loadCounts)

async function loadCounts() {
  try {
    counts.value = await api.getJson<CountsResponse>('/application/counts')
  } catch (ignore) {
  }
}
</script>

<template>
  <v-btn
      v-if="!!counts"
      class="counts-btn"
  >
    <v-icon :icon="mdiAccountMultiple" />
    : {{ counts.onlineUsers }}
    <v-tooltip
        activator="parent"
        location="bottom"
        open-delay="1000"
    >
      {{ onlineUsersTooltip }}
    </v-tooltip>
  </v-btn>
  <v-btn
      v-if="!!counts"
      class="counts-btn"
  >
    <v-icon :icon="mdiSwordCross" />
    : {{ counts.battles }}
    <v-tooltip
        activator="parent"
        location="bottom"
        open-delay="1000"
    >
      {{ battlesTooltip }}
    </v-tooltip>
  </v-btn>
</template>

<style scoped>
.counts-btn {
  padding: 0 8px;
}
</style>
