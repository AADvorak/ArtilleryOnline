<script setup lang="ts">
import {useRouter} from "#app";
import {useUserSettingsStore} from "~/stores/user-settings";
import {SoundSettingsNames} from "~/dictionary/sound-settings-names";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
const router = useRouter()
const userSettingsStore = useUserSettingsStore()

const settings = reactive({
  enable: '0',
  volume: '100'
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

watch(() => settings.volume, value => {
  const strValue = value.toString()
  const existingValue = soundSettings.value[SoundSettingsNames.VOLUME]
  if (strValue && strValue !== existingValue) {
    setTimeout(() => {
      if (strValue === settings.volume.toString()) {
        userSettingsStore.setSoundSetting({
          name: SoundSettingsNames.VOLUME,
          value: strValue
        })
      }
    }, 600)
  }
})

onMounted(() => {
  settings.enable = soundSettings.value[SoundSettingsNames.ENABLE]
  settings.volume = soundSettings.value[SoundSettingsNames.VOLUME]
})

function back() {
  router.push('/settings')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        <menu-navigation/>
      </v-card-title>
      <v-card-text>
        <v-table class="mb-4" density="compact">
          <tbody>
          <tr>
            <td>{{ t('sounds.enableSounds') }}</td>
            <td>
              <v-switch
                  v-model="settings.enable"
                  true-value="1"
                  false-value="0"
              />
            </td>
          </tr>
          <tr>
            <td>{{ t('sounds.volume') }}</td>
            <td>
              <v-slider
                  v-model="settings.volume"
                  :max="100"
                  :min="0"
                  :step="1"
                  class="align-center"
                  hide-details
              >
                <template v-slot:append>
                  <v-text-field
                      v-model="settings.volume"
                      density="compact"
                      style="width: 90px"
                      type="number"
                      hide-details
                      single-line
                  ></v-text-field>
                </template>
              </v-slider>
            </td>
          </tr>
          </tbody>
        </v-table>
        <v-btn class="mb-4" width="100%" @click="back">{{ t('common.back') }}</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
