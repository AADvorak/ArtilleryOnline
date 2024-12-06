<template>
  <v-app full-height theme="dark">
    <div class="d-flex align-center justify-center flex-column default">
      <slot />
    </div>
    <locale />
  </v-app>
</template>

<style scoped>
.default {
  height: 100%;
}
</style>
