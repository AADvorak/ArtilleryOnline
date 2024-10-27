<script setup lang="ts">

import RoomInvitationDialog from "~/components/room-invitation-dialog.vue";
</script>

<template>
  <v-app full-height theme="dark">
    <div class="d-flex align-center justify-center flex-column default">
      <slot />
    </div>
    <room-invitation-dialog />
  </v-app>
</template>

<style scoped>
.default {
  height: 100%;
}
</style>
