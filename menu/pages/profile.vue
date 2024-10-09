<script setup lang="ts">
import {useRouter} from "#app";

const router = useRouter()

function save() {

}

function back() {
  router.push('/menu')
}
</script>

<template>
  <v-card width="100%" max-width="600px">
    <v-card-title>
      Artillery online: profile
    </v-card-title>
    <v-card-text>
      <v-form @submit.prevent="save">
        <v-text-field label="Nickname"/>
      </v-form>
      <v-btn class="mb-4" width="100%" color="success" @click="save">Save</v-btn>
      <v-btn class="mb-4" width="100%" @click="back">Back</v-btn>
    </v-card-text>
  </v-card>
</template>
