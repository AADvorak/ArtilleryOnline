<script setup lang="ts">

import MessagesMenu from "~/components/messages-menu.vue";
</script>

<template>
  <v-app full-height theme="dark">
    <v-app-bar>
      <v-spacer />
      <messages-menu />
    </v-app-bar>
    <div class="d-flex align-center justify-center flex-column default">
      <slot />
    </div>
  </v-app>
</template>

<style scoped>
.default {
  height: 100%;
}
</style>
