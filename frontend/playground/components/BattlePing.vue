<script setup lang="ts">
import {computed} from "vue";
import {useStompClientStore} from "~/stores/stomp-client";

const stompClientStore = useStompClientStore()

const pingTime = computed(() => stompClientStore.pingTime)

const pingTimeRestricted = computed(() => {
  if (!pingTime.value || pingTime.value < 0) {
    return 0
  }
  return pingTime.value
})

const pingClass = computed(() => {
  if (pingTime.value < 50) {
    return 'battle-ping-green'
  } else if (pingTime.value < 100) {
    return 'battle-ping-orange'
  } else {
    return 'battle-ping-red'
  }
})
</script>

<template>
  <div :class="pingClass">
    Ping
    <span v-if="pingTime < 1000">{{ pingTimeRestricted }}</span>
    <span v-else>&infin;</span>
    ms
  </div>
</template>

<style scoped>
.battle-ping-green {
  color: green;
  font-size: 14px;
  font-weight: bold;
}

.battle-ping-orange {
  color: orange;
  font-size: 14px;
  font-weight: bold;
}

.battle-ping-red {
  color: crimson;
  font-size: 14px;
  font-weight: bold;
}
</style>
