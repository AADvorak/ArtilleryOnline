<script setup lang="ts">
import {useRouter} from "#app";
import {useUserStore} from "~/stores/user";
import {ApiRequestSender} from "~/api/api-request-sender";
import type {EditUserRequest} from "~/data/request";
import type {User} from "~/data/model";
import {useFormSubmit} from "~/composables/form-submit";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
const router = useRouter()
const userStore = useUserStore()

const emailField = ref<HTMLInputElement | null>(null)

const form = reactive({
  email: '',
  nickname: ''
})
const validation = reactive({
  email: [],
  nickname: []
})
const submitting = ref<boolean>(false)

const noChanges = computed(() => {
  const user = userStore.user!
  return user.email === form.email && user.nickname === form.nickname
})

onMounted(() => {
  const user = userStore.user!
  form.email = user.email
  form.nickname = user.nickname
  emailField.value?.focus()
})

async function save() {
  await useFormSubmit({form, validation, submitting}).submit(async () => {
    const request = {
      email: form.email,
      nickname: form.nickname
    }
    useUserStore().user = await new ApiRequestSender().putJson<EditUserRequest, User>('/users/me', request)
  })
}

function back() {
  router.push('/user')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: {{ t('profile.title') }}
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
              v-model="form.nickname"
              :error="!!validation.nickname.length"
              :error-messages="validation.nickname"
              :label="t('common.nickname')"
          />
          <v-btn class="mb-4" width="100%" color="success" type="submit" :loading="submitting"
                 :disabled="noChanges" @click="save">{{ t('common.save') }}</v-btn>
          <v-btn class="mb-4" width="100%" @click="back">{{ t('common.back') }}</v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
