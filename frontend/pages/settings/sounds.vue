<script setup lang="ts">
import {useRouter} from "#app";
import {useUserSettingsStore} from "~/stores/user-settings";
import {SoundSettingsNames} from "~/dictionary/sound-settings-names";

const router = useRouter()
const userSettingsStore = useUserSettingsStore()

const settings = reactive({
  enable: '0'
})

const soundSettings = computed(() => userSettingsStore.soundSettingsOrDefaultsNameValueMapping)

watch(() => settings.enable, value => {
  const existingValue = soundSettings.value[SoundSettingsNames.ENABLE]
  if (value && value !== existingValue) {
    userSettingsStore.setSoundSetting({
      name: SoundSettingsNames.ENABLE,
      value
    })
  }
})

onMounted(() => {
  settings.enable = soundSettings.value[SoundSettingsNames.ENABLE]
})

function back() {
  router.push('/settings')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: settings / sounds
      </v-card-title>
      <v-card-text>
        <v-table class="mb-4" density="compact">
          <tbody>
          <tr>
            <td>Enable sounds</td>
            <td>
              <v-switch
                  v-model="settings.enable"
                  true-value="1"
                  false-value="0"
              />
            </td>
          </tr>
          </tbody>
        </v-table>
        <v-btn class="mb-4" width="100%" @click="back">Back</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
