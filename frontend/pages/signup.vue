<script setup lang="ts">
import {useRouter} from "#app";
import {VALIDATION_MSG} from "~/dictionary/validation-msg";
import {ApiRequestSender} from "~/api/api-request-sender";
import type {RegisterRequest} from "~/data/request";
import type {User} from "~/data/model";
import {useUserStore} from "~/stores/user";
import type {FormValidation, FormValues} from "~/data/response";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
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
const submitting = ref<boolean>(false)

onMounted(() => {
  emailField.value?.focus()
})

function logIn() {
  router.push('/login')
}

async function signUp() {
  await useFormSubmit({form, validation, submitting, validator}).submit(async () => {
    const request = {
      email: form.email,
      password: form.password,
      nickname: form.nickname
    }
    useUserStore().user = await new ApiRequestSender().postJson<RegisterRequest, User>('/users', request)
    await router.push('/menu')
  })
}

function validator(form: FormValues, validation: FormValidation): boolean {
  if (form.password !== form.passwordConfirm) {
    validation.password.push(VALIDATION_MSG.EQUAL)
    validation.passwordConfirm.push(VALIDATION_MSG.EQUAL)
    return false
  }
  return true
}
</script>

<template>
  <NuxtLayout name="unauthenticated">
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: {{ t('signup.title') }}
      </v-card-title>
      <v-card-text>
        <v-form @submit.prevent>
          <v-text-field
              ref="emailField"
              v-model="form.email"
              :error="!!validation.email.length"
              :error-messages="validation.email"
              :label="t('common.email')"
          />
          <v-text-field
              v-model="form.password"
              :error="!!validation.password.length"
              :error-messages="validation.password"
              type="password"
              :label="t('common.password')"
          />
          <v-text-field
              v-model="form.passwordConfirm"
              :error="!!validation.passwordConfirm.length"
              :error-messages="validation.passwordConfirm"
              type="password"
              :label="t('common.passwordConfirm')"
          />
          <v-text-field
              v-model="form.nickname"
              :error="!!validation.nickname.length"
              :error-messages="validation.nickname"
              :label="t('common.nickname')"
          />
          <v-btn class="mb-4" width="100%" color="primary" type="submit" :loading="submitting"
                 @click="signUp">{{ t('common.signUp') }}</v-btn>
          <v-btn class="mb-4" width="100%" color="secondary" @click="logIn">{{ t('common.logIn') }}</v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
