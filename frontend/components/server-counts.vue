<script setup lang="ts">
import {ApiRequestSender} from "~/api/api-request-sender";
import {mdiAccountMultiple, mdiSwordCross} from "@mdi/js";

interface CountsResponse {
  battles: number
  onlineUsers: number
}

const api = new ApiRequestSender()

const counts = ref<CountsResponse | undefined>()

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
  </v-btn>
  <v-btn
      v-if="!!counts"
      class="counts-btn"
  >
    <v-icon :icon="mdiSwordCross" />
    : {{ counts.battles }}
  </v-btn>
</template>

<style scoped>
.counts-btn {
  padding: 0 8px;
}
</style>
