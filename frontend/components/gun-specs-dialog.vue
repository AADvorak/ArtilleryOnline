<script setup lang="ts">
import {ref} from 'vue'
import {useI18n} from "vue-i18n";
import type {GunSpecs} from "~/playground/data/specs";
import GunShellsSpecsTables from "~/components/gun-shells-specs-tables.vue";

const props = defineProps<{
  gunName: string | undefined
  gunSpecs: GunSpecs | undefined
}>()

const {t} = useI18n()

const opened = ref(false)

function show() {
  opened.value = true
}

function hide() {
  opened.value = false
}

defineExpose({
  show
})
</script>

<template>
  <v-dialog :model-value="opened" :persistent="true" max-width="600px">
    <v-card v-if="!!props.gunSpecs && !!props.gunName" width="100%">
      <v-card-title>{{ t('gunSpecsDialog.title') }}: {{ t(`names.guns.${props.gunName}`) }}</v-card-title>
      <v-card-text>
        <gun-shells-specs-tables :gun-specs="props.gunSpecs" />
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hide">{{ t('common.ok') }}</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
