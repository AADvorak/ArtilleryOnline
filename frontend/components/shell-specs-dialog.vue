<script setup lang="ts">
import {ref} from 'vue'
import {useI18n} from "vue-i18n";
import type {GunSpecs, ShellSpecs} from "~/playground/data/specs";
import {ShellType} from "~/playground/data/common";

const {t} = useI18n()

const opened = ref(false)

const shellName = ref<string | undefined>()
const shellSpecs = ref<ShellSpecs | undefined>()
const gunSpecs = ref<GunSpecs | undefined>()

const specsToShow = computed(() => [
  {
    key: 'damage',
    value: calculateDamage()
  },
  {
    key: 'dpm',
    value: (60 * calculateDamage() / gunSpecs.value?.loadTime).toFixed(0)
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

function calculateDamage() {
  if (shellSpecs.value) {
    const damage = shellSpecs.value.damage
    return shellSpecs.value.type === ShellType.BMB ? 2 * damage : damage
  } else {
    return 0
  }
}

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
