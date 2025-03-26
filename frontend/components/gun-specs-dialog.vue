<script setup lang="ts">
import {ref} from 'vue'
import {useI18n} from "vue-i18n";
import type {GunSpecs} from "~/playground/data/specs";
import GunSpecsTable from "~/components/gun-specs-table.vue";

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
      <v-card-title>{{ t('gunSpecsDialog.title') }}: {{ props.gunName }}</v-card-title>
      <v-card-text>
        <gun-specs-table :gun-specs="props.gunSpecs" />
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hide">{{ t('common.ok') }}</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
