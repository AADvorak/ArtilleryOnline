<script setup lang="ts">
import {ref} from 'vue'
import {useI18n} from "vue-i18n";
import type {GunSpecs} from "~/playground/data/specs";

const props = defineProps<{
  gunName: string | undefined
  gunSpecs: GunSpecs | undefined
}>()

const {t} = useI18n()

const opened = ref(false)

const specsToShow = computed(() => [
  {
    key: 'loadTime',
    value: props.gunSpecs?.loadTime
  },
  {
    key: 'rotationVelocity',
    value: (180 * props.gunSpecs?.rotationVelocity / Math.PI).toFixed(2)
  },
  {
    key: 'caliber',
    value: props.gunSpecs?.caliber * 1000
  },
  {
    key: 'availableShells',
    value: Object.keys(props.gunSpecs?.availableShells || {})
        .reduce((a, b) => a + (a ? ', ' : '') + b, '')
  },
])

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
        <v-table density="compact">
          <tbody>
          <tr v-for="spec of specsToShow">
            <td>{{ t('gunSpecsDialog.' + spec.key) }}</td>
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
