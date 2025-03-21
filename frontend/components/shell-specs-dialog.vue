<script setup lang="ts">
import {ref} from 'vue'
import {useI18n} from "vue-i18n";
import type {ShellSpecs} from "~/playground/data/specs";

const {t} = useI18n()

const opened = ref(false)

const shellName = ref<string | undefined>()
const shellSpecs = ref<ShellSpecs | undefined>()

const specsToShow = computed(() => [
  {
    key: 'damage',
    value: shellSpecs.value?.damage
  },
  {
    key: 'velocity',
    value: shellSpecs.value?.velocity
  },
  {
    key: 'caliber',
    value: shellSpecs.value?.caliber * 1000
  },
  {
    key: 'type',
    value: t('shellSpecsDialog.types.' + shellSpecs.value?.type)
  },
])

function show(name: string, specs: ShellSpecs) {
  shellName.value = name
  shellSpecs.value = specs
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
    <v-card v-if="!!shellName && !!shellSpecs" width="100%">
      <v-card-title>{{ t('shellSpecsDialog.title') }}: {{ shellName }}</v-card-title>
      <v-card-text>
        <v-table density="compact">
          <tbody>
          <tr v-for="spec of specsToShow">
            <td>{{ t('shellSpecsDialog.' + spec.key) }}</td>
            <td>{{ spec.value }}</td>
          </tr>
          </tbody>
        </v-table>
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hide">{{ t('common.ok') }}</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
