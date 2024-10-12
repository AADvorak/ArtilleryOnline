<script setup lang="ts">
import {useRouter} from "#app";
import {VALIDATION_MSG} from "~/dictionary/validation-msg";
import {ApiRequestSender} from "~/api/api-request-sender";
import type {RegisterRequest} from "~/data/request";
import type {User} from "~/data/model";
import {useUserStore} from "~/stores/user";
import {useRequestErrorHandler} from "~/composables/request-error-handler";

const router = useRouter()

const emailField = ref<HTMLInputElement | null>(null)

const form = reactive({
  email: '',
  nickname: '',
  password: '',
  passwordConfirm: ''
})
const validation = reactive({
  email: [],
  nickname: [],
  password: [],
  passwordConfirm: []
})

onMounted(() => {
  emailField.value?.focus()
})

function signIn() {
  router.push('/sign-in')
}

async function signUp() {
  clearValidation()
  if (preValidateForm()) {
    const request = {
      email: form.email,
      password: form.password,
      nickname: form.nickname
    }
    try {
      useUserStore().user = await new ApiRequestSender().postJson<RegisterRequest, User>('/users', request)
      await router.push('/menu')
    } catch (e) {
      useRequestErrorHandler().handle(e, validation)
    }
  }
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
  if (form.password !== form.passwordConfirm) {
    valid = false
    validation.password.push(VALIDATION_MSG.EQUAL)
    validation.passwordConfirm.push(VALIDATION_MSG.EQUAL)
  }
  return valid
}
</script>

<template>
  <v-card width="100%" max-width="600px">
    <v-card-title>
      Artillery online: sign up
    </v-card-title>
    <v-card-text>
      <v-form @submit.prevent>
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
        <v-text-field
            v-model="form.passwordConfirm"
            :error="!!validation.passwordConfirm.length"
            :error-messages="validation.passwordConfirm"
            type="password"
            label="Password confirm"
        />
        <v-text-field
            v-model="form.nickname"
            :error="!!validation.nickname.length"
            :error-messages="validation.nickname"
            label="Nickname"
        />
        <v-btn class="mb-4" width="100%" color="primary" type="submit" @click="signUp">Sign up</v-btn>
        <v-btn class="mb-4" width="100%" color="secondary" @click="signIn">Sign in</v-btn>
      </v-form>
    </v-card-text>
  </v-card>
</template>
