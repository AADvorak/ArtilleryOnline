<script setup lang="ts">
import {computed} from "vue";
import {useStompClientStore} from "~/stores/stomp-client";

const stompClientStore = useStompClientStore()

const pingTime = computed(() => {
  return stompClientStore.pingTime
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
  <div :class="pingClass">Ping {{ pingTime }}ms</div>
</template>

<style scoped>
.battle-ping-green {
  color: green;
  font-size: large;
}

.battle-ping-orange {
  color: orange;
  font-size: large;
}

.battle-ping-red {
  color: crimson;
  font-size: large;
}
</style>
