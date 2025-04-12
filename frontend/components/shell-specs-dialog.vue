<script setup lang="ts">
import {ref} from 'vue'
import {useI18n} from "vue-i18n";
import type {GunSpecs, ShellSpecs} from "~/playground/data/specs";
import ShellSpecsTable from "~/components/shell-specs-table.vue";

const {t} = useI18n()

const opened = ref(false)

const shellName = ref<string | undefined>()
const shellSpecs = ref<ShellSpecs | undefined>()
const gunSpecs = ref<GunSpecs | undefined>()

function show(name: string, specs: ShellSpecs, gun: GunSpecs) {
  shellName.value = name
  shellSpecs.value = specs
  gunSpecs.value = gun
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
    <v-card v-if="!!shellName && !!shellSpecs && !!gunSpecs" width="100%">
      <v-card-title>{{ t('shellSpecsDialog.title') }}: {{ t(`names.shells.${shellName}`) }}</v-card-title>
      <v-card-text>
        <shell-specs-table :gun-specs="gunSpecs" :shell-specs="shellSpecs" />
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hide">{{ t('common.ok') }}</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
