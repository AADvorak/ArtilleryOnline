<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {AppearancesNames} from "~/dictionary/appearances-names";
import {useUserSettingsStore} from "~/stores/user-settings";

const i18n = useI18n()
const userSettingsStore = useUserSettingsStore()

const appearances = computed(() => userSettingsStore.appearancesOrDefaultsNameValueMapping)

onMounted(() => {
  if (appearances.value[AppearancesNames.LANGUAGE]) {
    i18n.locale.value = appearances.value[AppearancesNames.LANGUAGE]
  }
})

watch(appearances, value => {
  if (value[AppearancesNames.LANGUAGE]) {
    i18n.locale.value = value[AppearancesNames.LANGUAGE]
  }
})
</script>
