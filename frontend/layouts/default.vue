<script setup lang="ts">

import MessagesMenu from "~/components/messages-menu.vue";
</script>

<template>
  <v-app full-height theme="dark">
    <v-app-bar density="compact">
      <v-spacer />
      <messages-menu />
    </v-app-bar>
    <div class="d-flex align-center justify-center flex-column default">
      <slot />
    </div>
    <error-message />
    <connection-lost-dialog />
    <locale />
  </v-app>
</template>

<style scoped>
.default {
  height: 100%;
}
</style>
