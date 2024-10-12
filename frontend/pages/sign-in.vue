<script setup lang="ts">
import {useRouter} from "#app";
import {VALIDATION_MSG} from "~/dictionary/validation-msg";
import {useUserStore} from "~/stores/user";
import {ApiRequestSender} from "~/api/api-request-sender";
import type {LoginRequest} from "~/data/request";
import type {User} from "~/data/model";
import {useRequestErrorHandler} from "~/composables/request-error-handler";

const router = useRouter()

const emailField = ref<HTMLInputElement | null>(null)

const form = reactive({
  email: '',
  password: ''
})
const validation = reactive({
  email: [],
  password: []
})

onMounted(() => {
  emailField.value?.focus()
})

async function signIn() {
  clearValidation()
  if (preValidateForm()) {
    const request = {
      email: form.email,
      password: form.password
    }
    try {
      useUserStore().user = await new ApiRequestSender().postJson<LoginRequest, User>('/users/login', request)
      await router.push('/menu')
    } catch (e) {
      useRequestErrorHandler().handle(e, validation)
    }
  }
}

function signUp() {
  router.push('/sign-up')
}

function clearValidation() {
  Object.keys(validation).forEach(key => validation[key] = [])
}

function preValidateForm() {
  let valid = true
  Object.keys(form).forEach(key => {
    if (!form[key].length) {
      validation[key].push(VALIDATION_MSG.REQUIRED)
      valid = false
    }
  })
  return valid
}
</script>

<template>
  <v-card width="100%" max-width="600px">
    <v-card-title>
      Artillery online: sign in
    </v-card-title>
    <v-card-text>
      <v-form @submit.prevent="signIn">
        <v-text-field
            ref="emailField"
            v-model="form.email"
            :error="!!validation.email.length"
            :error-messages="validation.email"
            label="Email"
        />
        <v-text-field
            v-model="form.password"
            :error="!!validation.password.length"
            :error-messages="validation.password"
            type="password"
            label="Password"
        />
      </v-form>
      <v-btn class="mb-4" width="100%" color="primary" @click="signIn">Sign in</v-btn>
      <v-btn class="mb-4" width="100%" color="secondary" @click="signUp">Sign up</v-btn>
    </v-card-text>
  </v-card>
</template>
