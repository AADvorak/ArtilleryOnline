<template>
  <v-app full-height theme="dark">
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
