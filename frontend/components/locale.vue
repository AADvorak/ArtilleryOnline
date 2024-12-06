<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {AppearancesNames} from "~/dictionary/appearances-names";
import {useUserSettingsStore} from "~/stores/user-settings";

const i18n = useI18n()
const userSettingsStore = useUserSettingsStore()

const appearances = computed(() => userSettingsStore.appearancesMapping)

onMounted(() => {
  if (appearances.value[AppearancesNames.LANGUAGE]) {
    i18n.locale.value = appearances.value[AppearancesNames.LANGUAGE]
  } else {
    detectLocale()
  }
})

watch(appearances, value => {
  if (value[AppearancesNames.LANGUAGE]) {
    i18n.locale.value = value[AppearancesNames.LANGUAGE]
  }
})

function detectLocale() {
  if (navigator) {
    if (navigator.language) {
      if (trySetLocaleFromLanguage(navigator.language)) {
        return
      }
    }
    if (navigator.languages) {
      for (const language of navigator.languages) {
        if (trySetLocaleFromLanguage(language)) {
          return
        }
      }
    }
  }
}

function trySetLocaleFromLanguage(language) {
  const localeFromLanguage = extractLocaleFromLanguage(language)
  if (i18n.availableLocales.includes(localeFromLanguage)) {
    i18n.locale.value = localeFromLanguage
    return true
  }
  return false
}

function extractLocaleFromLanguage(language) {
  return language.split('-')[0]
}
</script>

<template>

</template>
