<script setup lang="ts">
import {useRouter} from "#app";
import {useUserStore} from "~/stores/user";
import {ApiRequestSender} from "~/api/api-request-sender";
import type {LoginRequest} from "~/data/request";
import type {User} from "~/data/model";
import {useFormSubmit} from "~/composables/form-submit";
import {useI18n} from "vue-i18n";
import {useValidationLocaleUtil} from "~/composables/validation-locale-util";

const {t} = useI18n()
const {localize} = useValidationLocaleUtil(t)
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
const submitting = ref<boolean>(false)

onMounted(() => {
  emailField.value?.focus()
})

async function logIn() {
  await useFormSubmit({form, validation, submitting}).submit(async () => {
    const request = {
      email: form.email,
      password: form.password
    }
    useUserStore().user = await new ApiRequestSender().postJson<LoginRequest, User>('/users/login', request)
    await router.push('/menu')
  })
}

function signUp() {
  router.push('/signup')
}
</script>

<template>
  <NuxtLayout name="unauthenticated">
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: {{ t('login.title') }}
      </v-card-title>
      <v-card-text>
        <v-form @submit.prevent>
          <v-text-field
              ref="emailField"
              v-model="form.email"
              :error="!!validation.email.length"
              :error-messages="localize(validation.email)"
              :label="t('common.email')"
          />
          <v-text-field
              v-model="form.password"
              :error="!!validation.password.length"
              :error-messages="localize(validation.password)"
              type="password"
              :label="t('common.password')"
          />
          <v-btn class="mb-4" width="100%" color="primary" type="submit" :loading="submitting"
                 @click="logIn">{{ t('common.logIn') }}</v-btn>
          <v-btn class="mb-4" width="100%" color="secondary" @click="signUp">{{ t('common.signUp') }}</v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
