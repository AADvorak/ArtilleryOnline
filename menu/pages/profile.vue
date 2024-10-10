<script setup lang="ts">
import {useRouter} from "#app";
import {useUserStore} from "~/stores/user";

const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  email: '',
  nickname: ''
})
const validation = reactive({
  email: [],
  nickname: []
})

onMounted(() => {
  const user = userStore.user!
  form.email = user.email
  form.nickname = user.nickname
})

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
        <v-text-field
            v-model="form.email"
            :error="!!validation.email.length"
            :error-messages="validation.email"
            label="Email"
        />
        <v-text-field
            v-model="form.nickname"
            :error="!!validation.nickname.length"
            :error-messages="validation.nickname"
            label="Nickname"
        />
      </v-form>
      <v-btn class="mb-4" width="100%" color="success" @click="save">Save</v-btn>
      <v-btn class="mb-4" width="100%" @click="back">Back</v-btn>
    </v-card-text>
  </v-card>
</template>
